package library;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;

public class BorrowService {

    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();

public void borrowBook(String memberID, String bookID) throws LibraryException {

    Member member = memberDAO.findMemberByID(memberID);
    if (member == null) {
        throw new LibraryException("Member ID not found: " + memberID);
    }

    Book book = bookDAO.findBookByID(bookID);
    if (book == null) {
        throw new LibraryException("Book ID not found: " + bookID);
    }

    if (book.getCopiesAvailable() <= 0) {
        throw new LibraryException("No copies available for book: " + book.getTitle());
    }

    String recordID = generateRecordID();
    LocalDate borrowDate = LocalDate.now();
    LocalDate dueDate = borrowDate.plusDays(SharedSpec.LOAN_PERIOD_DAYS);
    String returnStatus = SharedSpec.STATUS_BORROWED;

    String sql = "INSERT INTO borrowrecord (recordID, memberID, bookID, borrowDate, dueDate, returnStatus) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, recordID);
        pstmt.setString(2, memberID);
        pstmt.setString(3, bookID);
        pstmt.setString(4, DateHelper.toText(borrowDate));
        pstmt.setString(5, DateHelper.toText(dueDate));
        pstmt.setString(6, returnStatus);

        pstmt.executeUpdate();

    } catch (SQLException e) {
        throw new LibraryException("Failed to insert borrow record into database.", e);
    }

    bookDAO.changeCopies(bookID, -1);
}

public void returnBook(String recordID) throws LibraryException {
    String selectSql = "SELECT bookID, returnStatus FROM borrowrecord WHERE recordID = ?";
    String bookID = null;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(selectSql)) {

        pstmt.setString(1, recordID);

        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) {
            throw new LibraryException("Borrow record not found: " + recordID);
        }

        String currentStatus = rs.getString("returnStatus");
        if (SharedSpec.STATUS_RETURNED.equals(currentStatus)) {
            throw new LibraryException("Book has already been returned for record: " + recordID);
        }

        bookID = rs.getString("bookID");

    } catch (SQLException e) {
        throw new LibraryException("Error verifying borrow record.", e);
    }

    String updateSql = "UPDATE borrowrecord SET returnStatus = ? WHERE recordID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

        pstmt.setString(1, SharedSpec.STATUS_RETURNED);
        pstmt.setString(2, recordID);

        int rows = pstmt.executeUpdate();
        if (rows == 0) {
            throw new LibraryException("No borrow record was updated for ID: " + recordID);
        }

    } catch (SQLException e) {
        throw new LibraryException("Failed to update return status in database.", e);
    }

    bookDAO.changeCopies(bookID, 1);
}

public void deleteRecord(String recordID) throws LibraryException {

    String selectSql = "SELECT returnStatus FROM borrowrecord WHERE recordID = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(selectSql)) {

        pstmt.setString(1, recordID);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) {
            throw new LibraryException("Borrow record not found: " + recordID);
        }

        String currentStatus = rs.getString("returnStatus");
        if (SharedSpec.STATUS_BORROWED.equals(currentStatus)) {
            throw new LibraryException(
                "Cannot delete record " + recordID + " because the book is still on loan. Return the book first, then delete the record.");
        }

    } catch (SQLException e) {
        throw new LibraryException("Error checking borrow record before delete.", e);
    }

    String deleteSql = "DELETE FROM borrowrecord WHERE recordID = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

        pstmt.setString(1, recordID);

        int rows = pstmt.executeUpdate();
        if (rows == 0) {
            throw new LibraryException("No borrow record was deleted for ID: " + recordID);
        }

    } catch (SQLException e) {
        throw new LibraryException("Failed to delete borrow record.", e);
    }
}

public ArrayList<BorrowRecord> getAllRecords() throws LibraryException {
    ArrayList<BorrowRecord> records = new ArrayList<>();
    String sql = "SELECT recordID, memberID, bookID, borrowDate, dueDate, returnStatus FROM borrowrecord";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String recordID = rs.getString("recordID");
            String memberID = rs.getString("memberID");
            String bookID = rs.getString("bookID");
            LocalDate borrowDate = DateHelper.toDate(rs.getString("borrowDate"));
            LocalDate dueDate = DateHelper.toDate(rs.getString("dueDate"));
            String returnStatus = rs.getString("returnStatus");

            BorrowRecord record = new BorrowRecord(recordID, memberID, bookID, borrowDate, dueDate, returnStatus);
            records.add(record);
        }

    } catch (SQLException e) {
        throw new LibraryException("Failed to retrieve borrow records.", e);
    }

    return records;
}

public ArrayList<BorrowRecord> getOverdueRecords() throws LibraryException {
    ArrayList<BorrowRecord> allRecords = getAllRecords();
    ArrayList<BorrowRecord> overdueRecords = new ArrayList<>();

    for (BorrowRecord record : allRecords) {
        if (record.isOverdue()) {
            overdueRecords.add(record);
        }
    }

    return overdueRecords;
}

public ArrayList<BorrowRecord> sortRecordsByDueDate(ArrayList<BorrowRecord> records) {
    ArrayList<BorrowRecord> sortedList = new ArrayList<>(records);
    Collections.sort(sortedList);
    return sortedList;
}

private String generateRecordID() throws LibraryException {
    String sql = "SELECT MAX(recordID) FROM borrowrecord";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            String maxID = rs.getString(1);

            if (maxID == null || maxID.isEmpty()) {
                return "R0001";
            }

            int lastNumber = Integer.parseInt(maxID.substring(1));
            int nextNumber = lastNumber + 1;

            String digits = "" + nextNumber;
            while (digits.length() < 4) {
                digits = "0" + digits;
            }
            return "R" + digits;
        }

    } catch (SQLException e) {
        throw new LibraryException("Failed to generate next record ID.", e);
    } catch (NumberFormatException e) {
        throw new LibraryException(
            "A borrow record has an ID that is not in the expected R0000 format.", e);
    }

    return "R0001";
}
}

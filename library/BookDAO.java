package library;

import java.sql.*;
import java.util.ArrayList;

public class BookDAO {

    public void addBook(Book b) throws LibraryException {

        String sql = "INSERT INTO book (bookID, title, author, copiesAvailable) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, b.getBookID());
            pstm.setString(2, b.getTitle());
            pstm.setString(3, b.getAuthor());
            pstm.setInt(4, b.getCopiesAvailable());

            pstm.executeUpdate();

        } catch (SQLException e) {
            throw new LibraryException("Cannot add book. The Book ID may already exist.", e);
        }
    }

    public void updateBook(Book b) throws LibraryException {

        String sql = "UPDATE book SET title = ?, author = ?, copiesAvailable = ? WHERE bookID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, b.getTitle());
            pstm.setString(2, b.getAuthor());
            pstm.setInt(3, b.getCopiesAvailable());
            pstm.setString(4, b.getBookID());

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new LibraryException("No book found with ID " + b.getBookID());
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot update book " + b.getBookID(), e);
        }
    }

    public void deleteBook(String bookID) throws LibraryException {

    String checkSql = "SELECT recordID FROM borrowrecord WHERE bookID = ? AND returnStatus = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstm = conn.prepareStatement(checkSql)) {

        pstm.setString(1, bookID);
        pstm.setString(2, SharedSpec.STATUS_BORROWED);

        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            throw new LibraryException("Cannot delete book " + bookID + " because it is still on loan.");
        }

    } catch (SQLException e) {
        throw new LibraryException("Cannot check book's borrow records.", e);
    }

    String deleteSql = "DELETE FROM book WHERE bookID = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstm = conn.prepareStatement(deleteSql)) {

        pstm.setString(1, bookID);

        int rows = pstm.executeUpdate();

        if (rows == 0) {
            throw new LibraryException("No book found with ID " + bookID);
        }

    } catch (SQLException e) {
        throw new LibraryException("Cannot delete book " + bookID, e);
    }
}

    public ArrayList<Book> getAllBooks() throws LibraryException {

        ArrayList<Book> list = new ArrayList<Book>();

        String sql = "SELECT bookID, title, author, copiesAvailable FROM book";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {

                String bookID = rs.getString("bookID");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int copies = rs.getInt("copiesAvailable");

                Book b = new Book(bookID, title, author, copies);
                list.add(b);
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot read the book list.", e);
        }

        return list;
    }

    public ArrayList<Book> searchBooks(String criteria, String keyword) throws LibraryException {

        String column;

        if (SharedSpec.SEARCH_BY_TITLE.equals(criteria)) {
            column = "title";
        } else if (SharedSpec.SEARCH_BY_AUTHOR.equals(criteria)) {
            column = "author";
        } else {
            throw new LibraryException("Unknown search criteria: " + criteria);
        }

        String sql = "SELECT bookID, title, author, copiesAvailable FROM book WHERE " + column + " LIKE ?";

        ArrayList<Book> list = new ArrayList<Book>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, "%" + keyword + "%");

            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                String id = rs.getString("bookID");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int copies = rs.getInt("copiesAvailable");

                list.add(new Book(id, title, author, copies));
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot search books.", e);
        }

        return list;
    }

    public ArrayList<Book> sortBooks(ArrayList<Book> list, String criteria) {

        ArrayList<Book> copy = new ArrayList<Book>(list);

        for (int i = 0; i < copy.size() - 1; i++) {
            int smallest = i;

            for (int j = i + 1; j < copy.size(); j++) {
                if (comesFirst(copy.get(j), copy.get(smallest), criteria)) {
                    smallest = j;
                }
            }

            Book firstBook = copy.get(i);
            Book smallestBook = copy.get(smallest);

            copy.remove(smallest);
            copy.add(smallest, firstBook);

            copy.remove(i);
            copy.add(i, smallestBook);
        }

    return copy;
}

    private boolean comesFirst(Book a, Book b, String criteria) {

        if (SharedSpec.SORT_BY_TITLE.equals(criteria)) {
            return a.getTitle().compareTo(b.getTitle()) < 0;
        }

        if (SharedSpec.SORT_BY_AUTHOR.equals(criteria)) {
            return a.getAuthor().compareTo(b.getAuthor()) < 0;
        }

        if (SharedSpec.SORT_BY_COPIES.equals(criteria)) {
            return a.getCopiesAvailable() < b.getCopiesAvailable();
        }

        return false;
    }

    public Book findBookByID(String bookID) throws LibraryException {

        String sql = "SELECT bookID, title, author, copiesAvailable FROM book WHERE bookID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, bookID);

            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                String id = rs.getString("bookID");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int copies = rs.getInt("copiesAvailable");

                return new Book(id, title, author, copies);
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot search for book " + bookID, e);
        }

        return null;
    }

    public void changeCopies(String bookID, int delta) throws LibraryException {

        String sql = "UPDATE book SET copiesAvailable = copiesAvailable + ? WHERE bookID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, delta);
            pstm.setString(2, bookID);

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new LibraryException("No book found with ID " + bookID);
            }

        } catch (SQLException e) {
            throw new LibraryException("Cannot update the copy count for " + bookID, e);
        }
    }
}
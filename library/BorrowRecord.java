package library;

import java.time.*;

public class BorrowRecord implements Comparable<BorrowRecord> {

    private String recordID;
    private String memberID;
    private String bookID;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String returnStatus;

    public BorrowRecord(String recordID, String memberID, String bookID, LocalDate borrowDate, LocalDate dueDate, String returnStatus) {
        this.recordID = recordID;
        this.memberID = memberID;
        this.bookID = bookID;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnStatus = returnStatus;
    }

    public String getRecordID() {
        return recordID;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getBookID() {
        return bookID;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public boolean isOverdue() {
        if (SharedSpec.STATUS_RETURNED.equals(this.returnStatus)) {
            return false;
        }
        if (this.dueDate == null) {
            return false;
        }
        return this.dueDate.isBefore(LocalDate.now());
    }

    public int compareTo(BorrowRecord other) {

        if (this.dueDate == null && other.dueDate == null) {
            return 0;
        }
        if (this.dueDate == null) {
            return 1;
        }
        if (other.dueDate == null) {
            return -1;
        }

        return this.dueDate.compareTo(other.dueDate);
    }

    public String toString() {
        return recordID + " | Member: " + memberID + " | Book: " + bookID + " | Due: " + DateHelper.toText(dueDate);
    }
}

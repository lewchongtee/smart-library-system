package library;

public class Book {

    private String bookID;
    private String title;
    private String author;
    private int copiesAvailable;

public Book(String bookID, String title, String author, int copiesAvailable) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.copiesAvailable = copiesAvailable;
    }

public String getBookID() {
    return bookID;
}

public String getTitle() {
    return title;
}

public String getAuthor() {
    return author;
}

public int getCopiesAvailable() {
    return copiesAvailable;
}

public void setTitle(String title) {
    this.title = title;
}

public void setAuthor(String author) {
    this.author = author;
}

public void setCopiesAvailable(int copiesAvailable) {
    this.copiesAvailable = copiesAvailable;
}

public String toString() {
    return bookID + " - " + title + " by " + author + " (" + copiesAvailable + " available)";
}
}
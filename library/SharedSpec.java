package library;

public class SharedSpec {

    public static final String DB_URL =
        "jdbc:mysql://localhost:3306/library";

    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static final String DB_USER     = "java_user";
    public static final String DB_PASSWORD = "your_password";

    public static final int LOAN_PERIOD_DAYS = 14;

    public static final String STATUS_BORROWED = "Borrowed";
    public static final String STATUS_RETURNED = "Returned";

    public static final String TYPE_STUDENT = "Student";
    public static final String TYPE_STAFF   = "Staff";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TABLE_BOOK   = "book";
    public static final String TABLE_MEMBER = "member";
    public static final String TABLE_RECORD = "borrowrecord";

    public static final String[] BOOK_COLUMNS =
        { "Book ID", "Title", "Author", "Copies Available" };

    public static final String[] MEMBER_COLUMNS =
        { "Member ID", "Name", "Contact No", "Member Type" };

    public static final String[] RECORD_COLUMNS =
        { "Record ID", "Member ID", "Book ID", "Borrow Date", "Due Date", "Status" };

    public static final String SEARCH_BY_TITLE     = "Title";
    public static final String SEARCH_BY_AUTHOR    = "Author";
    public static final String SEARCH_BY_NAME      = "Name";
    public static final String SEARCH_BY_MEMBER_ID = "Member ID";

    public static final String[] BOOK_SEARCH_OPTIONS =
        { SEARCH_BY_TITLE, SEARCH_BY_AUTHOR };

    public static final String[] MEMBER_SEARCH_OPTIONS =
        { SEARCH_BY_NAME, SEARCH_BY_MEMBER_ID };

    public static final String SORT_BY_TITLE     = "Title";
    public static final String SORT_BY_AUTHOR    = "Author";
    public static final String SORT_BY_COPIES    = "Copies Available";
    public static final String SORT_BY_NAME      = "Name";
    public static final String SORT_BY_MEMBER_ID = "Member ID";
    public static final String SORT_BY_DUE_DATE  = "Due Date";

    private SharedSpec() { }
}
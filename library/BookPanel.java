package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BookPanel extends JPanel {
	private JTextField bookIDField;
	private JTextField titleField;
	private JTextField authorField;
	private JTextField copiesField;
	private JTextArea displayArea;
	private JTextField messageField;
	private BookDAO bookDAO;

	BookPanel() {
		setLayout(new BorderLayout());
		bookDAO = new BookDAO();

		bookIDField = new JTextField(15);
		titleField = new JTextField(15);
		authorField = new JTextField(15);
		copiesField = new JTextField(15);

		JPanel formPanel = new JPanel(new GridLayout(2, 4, 8, 8));
		formPanel.add(new JLabel("Book ID:", JLabel.RIGHT));
		formPanel.add(bookIDField);
		formPanel.add(new JLabel("Title:", JLabel.RIGHT));
		formPanel.add(titleField);
		formPanel.add(new JLabel("Author:", JLabel.RIGHT));
		formPanel.add(authorField);
		formPanel.add(new JLabel("Copies Available:", JLabel.RIGHT));
		formPanel.add(copiesField);

		JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
		formRow.add(formPanel);

		JButton addButton = new JButton("Add");
		JButton updateButton = new JButton("Update");
		JButton deleteButton = new JButton("Delete");
		JButton displayButton = new JButton("Display All");
		JButton titleButton = new JButton("Search by Title");
		JButton authorButton = new JButton("Search by Author");
		JButton sortTitleButton = new JButton("Sort by Title");
		JButton sortAuthorButton = new JButton("Sort by Author");
		JButton sortCopiesButton = new JButton("Sort by Copies");

		JPanel recordButtons = new JPanel(new GridLayout(1, 4, 8, 0));
		recordButtons.add(addButton);
		recordButtons.add(updateButton);
		recordButtons.add(deleteButton);
		recordButtons.add(displayButton);

		JPanel findButtons = new JPanel(new GridLayout(1, 5, 8, 0));
		findButtons.add(titleButton);
		findButtons.add(authorButton);
		findButtons.add(sortTitleButton);
		findButtons.add(sortAuthorButton);
		findButtons.add(sortCopiesButton);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		JPanel recordRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
		recordRow.add(recordButtons);

		JPanel findRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
		findRow.add(findButtons);

		buttonPanel.add(recordRow);
		buttonPanel.add(findRow);

		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
		buttonRow.add(buttonPanel);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(formRow, BorderLayout.NORTH);
		topPanel.add(buttonRow, BorderLayout.SOUTH);
		add(topPanel, BorderLayout.NORTH);

		displayArea = new JTextArea();
		displayArea.setEditable(false);
		displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		JPanel centrePanel = new JPanel(new BorderLayout());
		centrePanel.add(new JLabel("  "), BorderLayout.WEST);
		centrePanel.add(new JLabel("  "), BorderLayout.EAST);
		centrePanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);
		add(centrePanel, BorderLayout.CENTER);

		messageField = new JTextField();
		messageField.setEditable(false);
		add(messageField, BorderLayout.SOUTH);

		addButton.addActionListener(new AddButtonListener());
		updateButton.addActionListener(new UpdateButtonListener());
		deleteButton.addActionListener(new DeleteButtonListener());
		displayButton.addActionListener(new DisplayButtonListener());
		titleButton.addActionListener(new TitleSearchButtonListener());
		authorButton.addActionListener(new AuthorSearchButtonListener());
		sortTitleButton.addActionListener(new SortTitleButtonListener());
		sortAuthorButton.addActionListener(new SortAuthorButtonListener());
		sortCopiesButton.addActionListener(new SortCopiesButtonListener());
	}

	private class AddButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Book book = readBook();
				bookDAO.addBook(book);
				refreshDisplay(bookDAO.getAllBooks());
				showMessage("Book added.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private class UpdateButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Book book = readBook();
				bookDAO.updateBook(book);
				refreshDisplay(bookDAO.getAllBooks());
				showMessage("Book updated.");
			} catch (LibraryException e) {
				showError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private class DeleteButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(bookIDField.getText(), "Book ID");
				bookDAO.deleteBook(bookIDField.getText());
				refreshDisplay(bookDAO.getAllBooks());
				showMessage("Book deleted.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class DisplayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(bookDAO.getAllBooks());
				showMessage("Books displayed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class TitleSearchButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(titleField.getText(), "Title");
				refreshDisplay(bookDAO.searchBooks(SharedSpec.SEARCH_BY_TITLE, titleField.getText()));
				showMessage("Search completed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class AuthorSearchButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(authorField.getText(), "Author");
				refreshDisplay(bookDAO.searchBooks(SharedSpec.SEARCH_BY_AUTHOR, authorField.getText()));
				showMessage("Search completed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class SortTitleButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(bookDAO.sortBooks(bookDAO.getAllBooks(), SharedSpec.SORT_BY_TITLE));
				showMessage("Books sorted by title.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class SortAuthorButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(bookDAO.sortBooks(bookDAO.getAllBooks(), SharedSpec.SORT_BY_AUTHOR));
				showMessage("Books sorted by author.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class SortCopiesButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(bookDAO.sortBooks(bookDAO.getAllBooks(), SharedSpec.SORT_BY_COPIES));
				showMessage("Books sorted by copies.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private Book readBook() throws LibraryException {
		Validator.requireNotEmpty(bookIDField.getText(), "Book ID");
		Validator.requireNotEmpty(titleField.getText(), "Title");
		Validator.requireNotEmpty(authorField.getText(), "Author");
		return new Book(bookIDField.getText(), titleField.getText(), authorField.getText(),
				Validator.parsePositiveInt(copiesField.getText(), "Copies Available"));
	}

	private void refreshDisplay(ArrayList<Book> books) {
		String text = headings(SharedSpec.BOOK_COLUMNS, new int[] { 12, 25, 25, 18 });
		if (books.isEmpty()) {
			displayArea.setText(text + "No books found.\n");
			return;
		}
		for (Book book : books) {
			text = text + Validator.pad(book.getBookID(), 12) + Validator.pad(book.getTitle(), 25)
					+ Validator.pad(book.getAuthor(), 25) + Validator.pad("" + book.getCopiesAvailable(), 18) + "\n";
		}
		displayArea.setText(text);
	}

	private String headings(String[] columns, int[] widths) {
		String text = "";
		int total = 0;
		for (int i = 0; i < columns.length; i++) {
			text = text + Validator.pad(columns[i], widths[i]);
			total = total + widths[i];
		}
		text = text + "\n";
		while (total > 0) {
			text = text + "-";
			total--;
		}
		return text + "\n";
	}

	private void showMessage(String text) {
		messageField.setText(text);
	}

	private void showError(String text) {
		messageField.setText("ERROR: " + text);
	}

	private void clearFields() {
		bookIDField.setText("");
		titleField.setText("");
		authorField.setText("");
		copiesField.setText("");
	}
}
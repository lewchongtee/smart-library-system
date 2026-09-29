package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BorrowPanel extends JPanel {
	private JTextField memberIDField;
	private JTextField bookIDField;
	private JTextField recordIDField;
	private JTextArea displayArea;
	private JTextField messageField;
	private BorrowService borrowService;

	BorrowPanel() {
		setLayout(new BorderLayout());
		borrowService = new BorrowService();

		memberIDField = new JTextField(14);
		bookIDField = new JTextField(14);
		recordIDField = new JTextField(14);

		JButton borrowButton = new JButton("Borrow");
		JButton returnButton = new JButton("Return");
		JButton deleteButton = new JButton("Delete Record");
		JButton allButton = new JButton("Show All Records");
		JButton overdueButton = new JButton("Show Overdue Only");
		JButton sortButton = new JButton("Sort by Due Date");

		JLabel borrowTitle = new JLabel("Borrow a Book");
		JLabel returnTitle = new JLabel("Return / Delete a Record");
		JLabel viewTitle = new JLabel("View Records");
		borrowTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
		returnTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
		viewTitle.setFont(new Font("SansSerif", Font.BOLD, 12));

		JPanel borrowTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
		borrowTitleRow.add(borrowTitle);

		JPanel borrowRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		borrowRow.add(new JLabel("Member ID:"));
		borrowRow.add(memberIDField);
		borrowRow.add(new JLabel("Book ID:"));
		borrowRow.add(bookIDField);
		borrowRow.add(borrowButton);

		JPanel borrowSection = new JPanel(new BorderLayout());
		borrowSection.add(borrowTitleRow, BorderLayout.NORTH);
		borrowSection.add(borrowRow, BorderLayout.CENTER);

		JPanel returnTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
		returnTitleRow.add(returnTitle);

		JPanel returnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		returnRow.add(new JLabel("Record ID:"));
		returnRow.add(recordIDField);
		returnRow.add(returnButton);
		returnRow.add(deleteButton);

		JPanel returnSection = new JPanel(new BorderLayout());
		returnSection.add(returnTitleRow, BorderLayout.NORTH);
		returnSection.add(returnRow, BorderLayout.CENTER);

		JPanel viewTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
		viewTitleRow.add(viewTitle);

		JPanel viewRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		viewRow.add(allButton);
		viewRow.add(overdueButton);
		viewRow.add(sortButton);

		JPanel viewSection = new JPanel(new BorderLayout());
		viewSection.add(viewTitleRow, BorderLayout.NORTH);
		viewSection.add(viewRow, BorderLayout.CENTER);

		JPanel topPanel = new JPanel(new GridLayout(3, 1));
		topPanel.add(borrowSection);
		topPanel.add(returnSection);
		topPanel.add(viewSection);

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

		borrowButton.addActionListener(new BorrowButtonListener());
		returnButton.addActionListener(new ReturnButtonListener());
		deleteButton.addActionListener(new DeleteButtonListener());
		allButton.addActionListener(new AllButtonListener());
		overdueButton.addActionListener(new OverdueButtonListener());
		sortButton.addActionListener(new SortButtonListener());
	}

	private class BorrowButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(memberIDField.getText(), "Member ID");
				Validator.requireNotEmpty(bookIDField.getText(), "Book ID");
				borrowService.borrowBook(memberIDField.getText(), bookIDField.getText());
				refreshDisplay(borrowService.getAllRecords());
				showMessage("Book borrowed.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class ReturnButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(recordIDField.getText(), "Record ID");
				borrowService.returnBook(recordIDField.getText());
				refreshDisplay(borrowService.getAllRecords());
				showMessage("Book returned.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class DeleteButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(recordIDField.getText(), "Record ID");
				borrowService.deleteRecord(recordIDField.getText());
				refreshDisplay(borrowService.getAllRecords());
				showMessage("Borrow record deleted.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class AllButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(borrowService.getAllRecords());
				showMessage("Records displayed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class OverdueButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(borrowService.getOverdueRecords());
				showMessage("Overdue records displayed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class SortButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(borrowService.sortRecordsByDueDate(borrowService.getAllRecords()));
				showMessage("Records sorted by due date.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private void refreshDisplay(ArrayList<BorrowRecord> records) {
		String text = headings(SharedSpec.RECORD_COLUMNS, new int[] { 14, 14, 12, 14, 14, 14 });
		if (records.isEmpty()) {
			displayArea.setText(text + "No borrowing records found.\n");
			return;
		}
		for (BorrowRecord record : records) {
			String status = record.getReturnStatus();
			if (record.isOverdue()) {
				status = "OVERDUE";
			}
			text = text + Validator.pad(record.getRecordID(), 14) + Validator.pad(record.getMemberID(), 14)
					+ Validator.pad(record.getBookID(), 12)
					+ Validator.pad(DateHelper.toText(record.getBorrowDate()), 14)
					+ Validator.pad(DateHelper.toText(record.getDueDate()), 14) + Validator.pad(status, 14) + "\n";
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
		memberIDField.setText("");
		bookIDField.setText("");
		recordIDField.setText("");
	}
}
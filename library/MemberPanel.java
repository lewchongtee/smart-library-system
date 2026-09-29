package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MemberPanel extends JPanel {
	private JTextField memberIDField;
	private JTextField nameField;
	private JTextField contactField;
	private JTextField typeField;
	private JTextArea displayArea;
	private JTextField messageField;
	private MemberDAO memberDAO;

	MemberPanel() {
		setLayout(new BorderLayout());
		memberDAO = new MemberDAO();

		memberIDField = new JTextField(15);
		nameField = new JTextField(15);
		contactField = new JTextField(15);
		typeField = new JTextField(15);

		JPanel formPanel = new JPanel(new GridLayout(2, 4, 8, 8));
		formPanel.add(new JLabel("Member ID:", JLabel.RIGHT));
		formPanel.add(memberIDField);
		formPanel.add(new JLabel("Name:", JLabel.RIGHT));
		formPanel.add(nameField);
		formPanel.add(new JLabel("Contact No:", JLabel.RIGHT));
		formPanel.add(contactField);
		formPanel.add(new JLabel("Member Type (Student or Staff):", JLabel.RIGHT));
		formPanel.add(typeField);

		JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
		formRow.add(formPanel);

		JButton addButton = new JButton("Add");
		JButton updateButton = new JButton("Update");
		JButton deleteButton = new JButton("Delete");
		JButton displayButton = new JButton("Display All");
		JButton nameButton = new JButton("Search by Name");
		JButton idButton = new JButton("Search by Member ID");
		JButton sortButton = new JButton("Sort by Name");
		JButton sortIDButton = new JButton("Sort by Member ID");

		JPanel recordButtons = new JPanel(new GridLayout(1, 4, 6, 0));
		recordButtons.add(addButton);
		recordButtons.add(updateButton);
		recordButtons.add(deleteButton);
		recordButtons.add(displayButton);

		JPanel findButtons = new JPanel(new GridLayout(1, 4, 6, 0));
		findButtons.add(nameButton);
		findButtons.add(idButton);
		findButtons.add(sortButton);
		findButtons.add(sortIDButton);

		JPanel recordRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 3));
		recordRow.add(recordButtons);

		JPanel findRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 3));
		findRow.add(findButtons);

		JPanel buttonRow = new JPanel(new GridLayout(2, 1, 0, 0));
		buttonRow.add(recordRow);
		buttonRow.add(findRow);

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
		nameButton.addActionListener(new NameSearchButtonListener());
		idButton.addActionListener(new IdSearchButtonListener());
		sortButton.addActionListener(new SortButtonListener());
		sortIDButton.addActionListener(new SortIDButtonListener());
	}

	private class AddButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				memberDAO.addMember(readMember());
				refreshDisplay(memberDAO.getAllMembers());
				showMessage("Member added.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class UpdateButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				memberDAO.updateMember(readMember());
				refreshDisplay(memberDAO.getAllMembers());
				showMessage("Member updated.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class DeleteButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(memberIDField.getText(), "Member ID");
				memberDAO.deleteMember(memberIDField.getText());
				refreshDisplay(memberDAO.getAllMembers());
				showMessage("Member deleted.");
				clearFields();
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class DisplayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(memberDAO.getAllMembers());
				showMessage("Members displayed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class NameSearchButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(nameField.getText(), "Name");
				refreshDisplay(memberDAO.searchMembers(SharedSpec.SEARCH_BY_NAME, nameField.getText()));
				showMessage("Search completed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class IdSearchButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Validator.requireNotEmpty(memberIDField.getText(), "Member ID");
				refreshDisplay(memberDAO.searchMembers(SharedSpec.SEARCH_BY_MEMBER_ID, memberIDField.getText()));
				showMessage("Search completed.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class SortButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(memberDAO.sortMembers(memberDAO.getAllMembers(), SharedSpec.SORT_BY_NAME));
				showMessage("Members sorted by name.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private class SortIDButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				refreshDisplay(memberDAO.sortMembers(memberDAO.getAllMembers(), SharedSpec.SORT_BY_MEMBER_ID));
				showMessage("Members sorted by member ID.");
			} catch (LibraryException e) {
				showError(e.getMessage());
			}
		}
	}

	private Member readMember() throws LibraryException {
		Validator.requireNotEmpty(memberIDField.getText(), "Member ID");
		Validator.requireNotEmpty(nameField.getText(), "Name");
		Validator.requireDigitsOnly(contactField.getText(), "Contact No");
		Validator.requireNotEmpty(typeField.getText(), "Member Type");
		if (!typeField.getText().equals(SharedSpec.TYPE_STUDENT)
				&& !typeField.getText().equals(SharedSpec.TYPE_STAFF)) {
			throw new LibraryException("Member Type must be Student or Staff.");
		}
		return new Member(memberIDField.getText(), nameField.getText(), contactField.getText(), typeField.getText());
	}

	private void refreshDisplay(ArrayList<Member> members) {
		String text = headings(SharedSpec.MEMBER_COLUMNS, new int[] { 14, 25, 18, 16 });
		if (members.isEmpty()) {
			displayArea.setText(text + "No members found.\n");
			return;
		}
		for (Member member : members) {
			text = text + Validator.pad(member.getMemberID(), 14) + Validator.pad(member.getName(), 25)
					+ Validator.pad(member.getContactNo(), 18) + Validator.pad(member.getMemberType(), 16) + "\n";
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
		nameField.setText("");
		contactField.setText("");
		typeField.setText("");
	}
}
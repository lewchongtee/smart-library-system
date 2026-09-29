package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
	private JPanel cardPanel;
	private CardLayout cardLayout;

	MainFrame() {
		setTitle("Smart Library Borrowing and Tracking System");
		setSize(900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel navigationPanel = new JPanel();
		JButton booksButton = new JButton("Books");
		JButton membersButton = new JButton("Members");
		JButton borrowingButton = new JButton("Borrowing");
		navigationPanel.add(booksButton);
		navigationPanel.add(membersButton);
		navigationPanel.add(borrowingButton);
		add(navigationPanel, BorderLayout.NORTH);

		cardLayout = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.add(new BookPanel(), "Books");
		cardPanel.add(new MemberPanel(), "Members");
		cardPanel.add(new BorrowPanel(), "Borrowing");
		add(cardPanel, BorderLayout.CENTER);

		JPanel exitPanel = new JPanel();
		JButton exitButton = new JButton("Exit");
		exitPanel.add(exitButton);
		add(exitPanel, BorderLayout.SOUTH);

		booksButton.addActionListener(new BooksButtonListener());
		membersButton.addActionListener(new MembersButtonListener());
		borrowingButton.addActionListener(new BorrowingButtonListener());
		exitButton.addActionListener(new ExitButtonListener());
		setLocationRelativeTo(null);
	}

	private class BooksButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			cardLayout.show(cardPanel, "Books");
		}
	}

	private class MembersButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			cardLayout.show(cardPanel, "Members");
		}
	}

	private class BorrowingButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			cardLayout.show(cardPanel, "Borrowing");
		}
	}

	private class ExitButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.exit(0);
		}
	}

	public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            new MainFrame().setVisible(true);
        }
    });
}
}

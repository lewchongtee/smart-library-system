package library;

public class Validator {
	public static void requireNotEmpty(String value, String fieldName) throws LibraryException {
		if (value == null || value.isEmpty()) {
			throw new LibraryException(fieldName + " cannot be empty.");
		}
	}

	public static int parsePositiveInt(String value, String fieldName) throws LibraryException {
		requireNotEmpty(value, fieldName);
		try {
			int number = Integer.parseInt(value);
			if (number < 0) {
				throw new LibraryException(fieldName + " cannot be negative.");
			}
			return number;
		} catch (NumberFormatException e) {
			throw new LibraryException(fieldName + " must be a whole number.", e);
		}
	}

	public static void requireDigitsOnly(String value, String fieldName) throws LibraryException {
		requireNotEmpty(value, fieldName);
		for (int i = 0; i < value.length(); i++) {
			if (!Character.isDigit(value.charAt(i))) {
				throw new LibraryException(fieldName + " must contain digits only.");
			}
		}
	}

	public static String pad(String text, int width) {
		if (text == null) {
			text = "";
		}
		if (text.length() > width) {
			return text.substring(0, width);
		}
		while (text.length() < width) {
			text = text + " ";
		}
		return text;
	}

	private Validator() {
	}
}

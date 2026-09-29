package library;

import java.time.*;
import java.time.format.*;

public class DateHelper {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(SharedSpec.DATE_FORMAT);

	private DateHelper() { }

	public static String toText(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMATTER);
    }

	public static LocalDate toDate(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
	}
}
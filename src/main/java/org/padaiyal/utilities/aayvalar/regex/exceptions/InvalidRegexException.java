package org.padaiyal.utilities.aayvalar.regex.exceptions;

import java.util.Locale;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.regex.RegexUtility;

/**
 * Invalid regex exception. Thrown when there is an error in the regex pattern.
 */
public class InvalidRegexException extends Exception {

  static {
    I18nUtility.addResourceBundle(
        RegexUtility.class,
        RegexUtility.class.getSimpleName(),
        Locale.US
    );
  }

  /**
   * Thrown when a specified regex expression is invalid.
   *
   * @param regex The invalid regex expression.
   */
  public InvalidRegexException(String regex) {
    super(
        I18nUtility.getFormattedString(
            "RegexUtility.exception.invalidRegex",
            regex
        )
    );
  }

}

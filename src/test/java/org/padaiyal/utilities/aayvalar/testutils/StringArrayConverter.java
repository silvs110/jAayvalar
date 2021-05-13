package org.padaiyal.utilities.aayvalar.testutils;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.PropertyUtility;

/**
 * Converts a string into arrays of Strings.
 */
public class StringArrayConverter extends SimpleArgumentConverter {

  /**
   * Logger object used to log information and errors.
   */
  private static final Logger logger = LogManager.getLogger(StringArrayConverter.class);


  /**
   * Initializes all dependant values.
   */
  public static Runnable dependantValuesInitializer = () -> {
    I18nUtility.addResourceBundle(
        StringArrayConverter.class,
        StringArrayConverter.class.getSimpleName(),
        Locale.US
    );
    try {
      PropertyUtility.addPropertyFile(
          StringArrayConverter.class,
          StringArrayConverter.class.getSimpleName() + ".properties"
      );
    } catch (IOException e) {
      logger.warn(e);
    }
  };

  static {
    dependantValuesInitializer.run();
  }


  /**
   * Private constructor.
   */
  private StringArrayConverter() {

  }

  /**
   * Converts a string into an array of specified types.
   *
   * @param arrayObject The string to convert.
   * @param targetType  The type of array to convert it to.
   * @throws ArgumentConversionException When the provided targetType is not supported.
   */
  public static Object[] convertArrayStringToArray(Object arrayObject, Class<?> targetType) {
    if (arrayObject == null) {
      return null;
    }
    Objects.requireNonNull(targetType);
    dependantValuesInitializer.run();
    if (arrayObject instanceof String && String[].class.isAssignableFrom(targetType)) {
      String arrayString = (String) arrayObject;
      return arrayString.length() <= 2
          ? new String[]{} : arrayString.substring(1, arrayString.length() - 1)
          .split(PropertyUtility.getProperty("StringArrayConverter.commaSeparatedElements.regex"));
    } else {
      throw new ArgumentConversionException(
          I18nUtility.getFormattedString(
              "StringArrayConverter.error.conversionNotSupported",
              arrayObject.getClass().getName(), targetType.getName()
          )
      );
    }
  }

  /**
   * Converts a string into an array of specified types.
   *
   * @param source     The string to convert.
   * @param targetType The type of array to convert it to.
   * @throws ArgumentConversionException When the provided targetType is not supported.
   */
  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
    return convertArrayStringToArray(source, targetType);
  }

}
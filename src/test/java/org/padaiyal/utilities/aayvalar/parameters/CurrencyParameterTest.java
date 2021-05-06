package org.padaiyal.utilities.aayvalar.parameters;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the CurrencyParameter.
 */
public class CurrencyParameterTest {

  /**
   * API key.
   * Specify the API key to run the valid input tests and also remove CurrencyParameter from
   * jacoco exclusion list.
   * If it is null, those tests will be skipped.
   */
  private final String apiKey = null;

  /**
   * Tests the MeasurableParameter::convertTo() method of the parameter being tested with valid
   * inputs.
   *
   * @param currentValue ValueT of the current parameter.
   * @param currentUnit  UnitT of the current parameter.
   * @param resultUnit   Desired unit to convert the current parameter.
   * @throws IOException When there is an issue reading the API response.
   */
  @ParameterizedTest
  @CsvSource({
      "1, USD, CAD",
      "3, CAD, USD",
      "2, USD, INR",
      "3.14, INR, USD",
      "4.5, CAD, INR",
      "90, INR, CAD",
      "10, GBP, USD",
      "1000, USD, GBP",
      "100000001, GBP, USD"
  })
  void testConvertToWithValidInputs(Double currentValue, String currentUnit, String resultUnit)
      throws IOException {
    //noinspection ConstantConditions
    Assumptions.assumeTrue(apiKey != null);

    CurrencyParameter currencyParameter = new CurrencyParameter(currentValue, currentUnit, apiKey);

    Assertions.assertEquals(currentValue, currencyParameter.getValue());

    Assertions.assertEquals(currentUnit, currencyParameter.getUnit());

    double resultValue = currencyParameter.convertTo(resultUnit);
    Assertions.assertTrue(resultValue > 0);
  }

  /**
   * Tests the MeasurableParameter::convertTo() method of the parameter being tested with invalid
   * inputs.
   *
   * @param currentValue           ValueT of the current parameter.
   * @param currentUnit            UnitT of the current parameter.
   * @param resultUnit             Desired unit to convert the current parameter.
   * @param expectedExceptionClass Expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      ", USD, CAD, java.lang.NullPointerException",
      "3,, USD, java.lang.NullPointerException",
      "2, USD,, java.lang.NullPointerException",
      "-3.14, INR, USD, java.lang.IllegalArgumentException",
      "3.14, TEST, USD, java.lang.IllegalArgumentException",
      "3.14, INR, USDA, java.lang.IllegalArgumentException",
  })
  void testConvertToWithInvalidInputs(
      Double currentValue,
      String currentUnit,
      String resultUnit,
      Class<? extends Exception> expectedExceptionClass
  ) {
    Assertions.assertThrows(
        expectedExceptionClass,
        () -> {
          @SuppressWarnings("ConstantConditions")
          CurrencyParameter currencyParameter = new CurrencyParameter(
              currentValue,
              currentUnit,
              apiKey
          );
          currencyParameter.convertTo(resultUnit);
        }
    );
  }
}

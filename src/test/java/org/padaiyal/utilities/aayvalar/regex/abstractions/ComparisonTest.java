package org.padaiyal.utilities.aayvalar.regex.abstractions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the methods in the Comparison enum.
 */
public class ComparisonTest {

  /**
   * Test evaluating comparisons between two numbers.
   *
   * @param number1        The first number to compare.
   * @param number2        The second number to compare.
   * @param comparison     The type of comparison.
   * @param expectedResult The expected result of the evaluation.
   */
  @ParameterizedTest
  @CsvSource({
      "1, 3, LESSER, true",
      "1, 3, LESSER_OR_EQUAL, true",
      "1, 3, EQUAL, false",
      "1, 3, GREATER, false",
      "5, -3, GREATER, true",
      "1, 3, GREATER_OR_EQUAL, false",
      "1, 3, UNEQUAL, true",
      "-34, 34, UNEQUAL, true",
      "-34, -34, UNEQUAL, false"
  })
  public void testEvaluateComparisons(
      int number1,
      int number2,
      Comparison comparison,
      boolean expectedResult
  ) {
    Assertions.assertEquals(expectedResult, comparison.evaluateComparison(number1, number2));
  }
}

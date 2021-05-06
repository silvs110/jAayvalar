package org.padaiyal.utilities.aayvalar.regex.abstractions;


import java.math.BigDecimal;

/**
 * Something.
 */
public enum Comparison {
  EQUAL,
  UNEQUAL,
  GREATER,
  GREATER_OR_EQUAL,
  LESSER,
  LESSER_OR_EQUAL;

  /**
   * Evaluates the current comparison operation against the specified numbers.
   *
   * @param number1 Number to compare on the left hand side of the operator.
   * @param number2 Number to compare on the right hand side of the operator.
   * @return Result of the comparison operation.
   */
  public boolean evaluateComparison(Number number1, Number number2) {
    boolean result = false;
    BigDecimal num1 = BigDecimal.valueOf(number1.doubleValue());
    BigDecimal num2 = BigDecimal.valueOf(number2.doubleValue());

    if (this == EQUAL || this == GREATER_OR_EQUAL || this == LESSER_OR_EQUAL) {
      result = num1.compareTo(num2) == 0;
    } else if (this == UNEQUAL) {
      result = num1.compareTo(num2) != 0;
    }

    if (this == Comparison.LESSER || this == Comparison.LESSER_OR_EQUAL) {
      result |= num1.compareTo(num2) < 0;
    } else if (this == Comparison.GREATER || this == Comparison.GREATER_OR_EQUAL) {
      result |= num1.compareTo(num2) > 0;
    }
    return result;
  }
}

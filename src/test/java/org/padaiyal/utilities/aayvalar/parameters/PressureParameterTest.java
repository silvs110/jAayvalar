package org.padaiyal.utilities.aayvalar.parameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.parameters.units.PressureUnitEnum;

/**
 * Tests the PressureParameter.
 */
public class PressureParameterTest
    extends MeasurableParameterTest<Double, PressureUnitEnum, PressureParameter> {

  @Override
  PressureParameter instantiateParameter(Double value, PressureUnitEnum unit) {
    return new PressureParameter(value, unit);
  }

  @Override
  PressureUnitEnum getExpectedSiUnit() {
    return PressureUnitEnum.PASCAL;
  }

  @Override
  Class<Double> getExpectedValueType() {
    return Double.class;
  }

  @ParameterizedTest
  @CsvSource({
      "1, PASCAL, 9.869232667160129E-6, ATMOSPHERIC",
      "1, PASCAL, 1.0E-5, BAR",
      "1, PASCAL, 0.007500637554192106, TORR",

      "3.141569, PASCAL, 3.141569, PASCAL",

      "1, ATMOSPHERIC, 101325.0, PASCAL",
      "1, BAR, 1.0E5, PASCAL",
      "1, TORR, 133.322, PASCAL"
  })
  @Override
  void testConvertToWithValidInputs(
      Double currentValue,
      PressureUnitEnum currentUnit,
      Double resultValue,
      PressureUnitEnum resultUnit
  ) {
    super.testConvertToWithValidInputs(
        currentValue,
        currentUnit,
        resultValue,
        resultUnit
    );
  }

  @ParameterizedTest
  @CsvSource({
      "-1, PASCAL, ATMOSPHERIC, java.lang.IllegalArgumentException",
      ", PASCAL, ATMOSPHERIC, java.lang.NullPointerException",
      "1,, ATMOSPHERIC, java.lang.NullPointerException",
      "1, PASCAL,, java.lang.NullPointerException",
      "1, PASCAL, UNKNOWN, java.lang.UnsupportedOperationException",
      "1, UNKNOWN, PASCAL, java.lang.UnsupportedOperationException"
  })
  @Override
  void testConvertToWithInvalidInputs(
      Double currentValue,
      PressureUnitEnum currentUnit,
      PressureUnitEnum resultUnit,
      Class<? extends Exception> expectedExceptionClass
  ) {
    super.testConvertToWithInvalidInputs(
        currentValue,
        currentUnit,
        resultUnit,
        expectedExceptionClass
    );
  }
}

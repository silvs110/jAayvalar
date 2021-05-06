package org.padaiyal.utilities.aayvalar.parameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.parameters.units.VolumeUnitEnum;

/**
 * Tests the VolumeParameter.
 */
public class VolumeParameterTest
    extends MeasurableParameterTest<Double, VolumeUnitEnum, VolumeParameter> {

  @Override
  VolumeParameter instantiateParameter(Double value, VolumeUnitEnum unit) {
    return new VolumeParameter(value, unit);
  }

  @Override
  VolumeUnitEnum getExpectedSiUnit() {
    return VolumeUnitEnum.LITER;
  }

  @Override
  Class<Double> getExpectedValueType() {
    return Double.class;
  }

  @ParameterizedTest
  @CsvSource({
      "1, LITER, 4.16665674260697, US_CUP",
      "1, LITER, 0.26417142315993614, US_GALLON",
      "1, LITER, 2.113371385279489, US_PINT",
      "1, LITER, 1.0566856926397445, US_QUART",
      "1, LITER, 67.62788432926665, US_TABLESPOON",
      "1, LITER, 202.88420181297323, US_TEASPOON",

      "3.141569, LITER, 3.141569, LITER",

      "1, US_CUP, 0.2400005716272, LITER",
      "1, US_GALLON, 3.7854208000180791238, LITER",
      "1, US_PINT, 0.47317760000225989048, LITER",
      "1, US_QUART, 0.94635520000451978095, LITER",
      "1, US_TABLESPOON, 0.0147868, LITER",
      "1, US_TEASPOON, 0.00492892, LITER"
  })
  @Override
  void testConvertToWithValidInputs(
      Double currentValue,
      VolumeUnitEnum currentUnit,
      Double resultValue,
      VolumeUnitEnum resultUnit
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
      "-1, LITER, US_CUP, java.lang.IllegalArgumentException",
      ", LITER, US_GALLON, java.lang.NullPointerException",
      "1,, US_GALLON, java.lang.NullPointerException",
      "1, LITER,, java.lang.NullPointerException",
      "1, LITER, UNKNOWN, java.lang.UnsupportedOperationException",
      "1, UNKNOWN, LITER, java.lang.UnsupportedOperationException"
  })
  @Override
  void testConvertToWithInvalidInputs(
      Double currentValue,
      VolumeUnitEnum currentUnit,
      VolumeUnitEnum resultUnit,
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
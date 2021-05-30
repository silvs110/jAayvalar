package org.padaiyal.utilities.aayvalar.parameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.parameters.units.DistanceUnitEnum;

/**
 * Tests the DistanceParameter.
 */
public class DistanceParameterTest
    extends MeasurableParameterTest<Double, DistanceUnitEnum, DistanceParameter> {

  @Override
  DistanceParameter instantiateParameter(Double value, DistanceUnitEnum unit) {
    return new DistanceParameter(value, unit);
  }

  @Override
  DistanceUnitEnum getExpectedSiUnit() {
    return DistanceUnitEnum.METRE;
  }

  @Override
  Class<Double> getExpectedValueType() {
    return Double.class;
  }

  @ParameterizedTest
  @CsvSource({
      "1, METRE, 1.0E10, ANGSTROM",
      "1, METRE, 1.0E15, FERMI",
      "1, METRE, 3.280839895013123, FOOT",
      "1, METRE, 39.37007874015748, INCH",
      "1, METRE, 1000000.0, MICRON",
      "1, METRE, 6.213727366498068E-4, MILE",
      "1, METRE, 1.0936132983377078, YARD",

      "3.141569, METRE, 3.141569, METRE",

      "1, ANGSTROM, 1.0E-10, METRE",
      "1, FERMI, 1.0E-15, METRE",
      "1, FOOT, 0.3048, METRE",
      "1, INCH, 0.0254, METRE",
      "1, MICRON, 1.0E-6, METRE",
      "1, MILE, 1609.34, METRE",
      "1, YARD, 0.9144, METRE"
  })
  @Override
  void testConvertToWithValidInputs(
      Double currentValue,
      DistanceUnitEnum currentUnit,
      Double resultValue,
      DistanceUnitEnum resultUnit
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
      "-1, METRE, ANGSTROM, java.lang.IllegalArgumentException",
      ", METRE, ANGSTROM, java.lang.NullPointerException",
      "1,, ANGSTROM, java.lang.NullPointerException",
      "1, METRE,, java.lang.NullPointerException",
      "1, METRE, UNKNOWN, java.lang.UnsupportedOperationException",
      "1, UNKNOWN, METRE, java.lang.UnsupportedOperationException"
  })
  @Override
  void testConvertToWithInvalidInputs(
      Double currentValue,
      DistanceUnitEnum currentUnit,
      DistanceUnitEnum resultUnit,
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

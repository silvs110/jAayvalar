package org.padaiyal.utilities.aayvalar.parameters;

import org.junit.jupiter.api.Assertions;
import org.padaiyal.utilities.aayvalar.parameters.units.UnitTypeEnumInterface;

/**
 * Abstracts the unit tests for measurable parameters.
 *
 * @param <ValueT>      Type of value in parameter.
 * @param <UnitT>       Unit used in parameter.
 * @param <ParameterT>  Type of parameter.
 */
public abstract class MeasurableParameterTest<
    ValueT,
    UnitT extends UnitTypeEnumInterface<ValueT>,
    ParameterT extends MeasurableParameter<ValueT, UnitT>
    > {

  /**
   * Instantiates the desired parameter.
   *
   * @param value ValueT of the desired parameter.
   * @param unit  UnitT of the desired parameter.
   * @return      The instantiated parameter.
   */
  abstract ParameterT instantiateParameter(ValueT value, UnitT unit);

  /**
   * Returns the expected SI unit of the parameter being tested.
   *
   * @return The expected SI unit of the parameter being tested.
   */
  abstract UnitT getExpectedSiUnit();

  /**
   * Returns the expected value type of the parameter being tested.
   *
   * @return The expected value type of the parameter being tested.
   */
  abstract Class<ValueT> getExpectedValueType();

  /**
   * Tests the MeasurableParameter::convertTo() method of the parameter being tested with valid
   * inputs.
   *
   * @param currentValue ValueT of the current parameter.
   * @param currentUnit  UnitT of the current parameter.
   * @param resultValue  Desired value of the converted parameter.
   * @param resultUnit   Desired unit to convert the current parameter.
   */
  void testConvertToWithValidInputs(
      ValueT currentValue,
      UnitT currentUnit,
      ValueT resultValue,
      UnitT resultUnit
  ) {
    ParameterT parameter = instantiateParameter(currentValue, currentUnit);

    Assertions.assertEquals(
        currentValue,
        parameter.getValue()
    );

    Assertions.assertEquals(
        currentUnit,
        parameter.getUnit()
    );

    Assertions.assertEquals(
        resultValue,
        parameter.convertTo(resultUnit)
    );

    Assertions.assertEquals(
        getExpectedSiUnit(),
        currentUnit.getSiUnit()
    );

    Assertions.assertEquals(
        getExpectedSiUnit(),
        resultUnit.getSiUnit()
    );

    Assertions.assertEquals(
        getExpectedValueType(),
        currentUnit.getValueType()
    );
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
  void testConvertToWithInvalidInputs(
      ValueT currentValue,
      UnitT currentUnit,
      UnitT resultUnit,
      Class<? extends Exception> expectedExceptionClass
  ) {
    Assertions.assertThrows(
        expectedExceptionClass,
        () -> instantiateParameter(currentValue, currentUnit)
            .convertTo(resultUnit)
    );
  }
}
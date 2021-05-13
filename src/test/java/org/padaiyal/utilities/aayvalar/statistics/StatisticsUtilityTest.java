package org.padaiyal.utilities.aayvalar.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.testutils.ExceptionClassConverter;
import org.padaiyal.utilities.aayvalar.testutils.StringArrayConverter;

/**
 * Test Functionality of StatisticsUtilityTest.
 */
@SuppressWarnings("SpellCheckingInspection")
public class StatisticsUtilityTest {

  /**
   * Convert a string to a numeric type.
   *
   * @param numericType The numeric type to convert it to.
   * @param number      The number to convert.
   * @return The numeric value of the input string.
   */
  private Number convertToNumber(String numericType, String number) {
    return switch (numericType) {
      case "int" -> Integer.parseInt(number);
      case "long" -> Long.parseLong(number);
      case "float" -> Float.parseFloat(number);
      case "double" -> Double.parseDouble(number);
      default -> throw new IllegalStateException();
    };
  }


  /**
   * Convert an array of Strings to the specified collection with elements of specified numeric
   * type.
   *
   * @param stringValues   The String array with the string representation of the numbers.
   * @param numericType    The numeric type to convert it to.
   * @param collectionType The collectionType to convert it to.
   * @return A collection with elements of specified numeric type.
   */
  private Collection<Number> convertToCollection(String[] stringValues, String numericType,
      String collectionType) {
    Stream<Number> stream = Arrays.stream(stringValues).parallel()
        .map(element -> convertToNumber(numericType, element));
    return switch (collectionType) {
      case "list" -> stream.collect(Collectors.toList());
      case "set" -> stream.collect(Collectors.toSet());
      //noinspection SpellCheckingInspection
      case "linkedlist" -> stream.collect(Collectors.toCollection(LinkedList::new));
      default -> throw new IllegalStateException();
    };
  }

  /**
   * Test calculating the average of all the elements in a collection.
   *
   * @param numericType    The numeric type to test.
   * @param collectionType The collection type to test.
   * @param expectedMean   The expected mean.
   * @param meanType       The mean type (arithmetic, geometric, harmonic).
   * @param stringValues   The array with the string representation of the values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int,list,5.0,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "long,list,5.0,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "float,list,3.62,ARITHMETIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      "double,list,3.62,ARITHMETIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      // Test different collection types
      "double,set,5.0,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "double,linkedlist,5.0,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      // Test with different means
      "double,list,2.653028861687813,GEOMETRIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      "double,list,1.976971644704602,HARMONIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
  })
  public void testGetMeanWithValidInputs(String numericType, String collectionType,
      double expectedMean, MeanType meanType,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {

    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    double actualMean = StatisticsUtility.getMean(inputCollection, meanType);
    Assertions.assertEquals(expectedMean, actualMean);
  }

  /**
   * Test calculating the mode from a collection of numbers.
   *
   * @param numericType    The numeric type to test.
   * @param collectionType The collection type to test.
   * @param expectedMode   The expected mode.
   * @param stringValues   The array with the string representation of the values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int,list,1,'[0,0,0,0,1,1,1,1,1,2,3,4,5]'",
      "long,list,1,'[0,0,0,0,1,1,1,1,1,2,3,4,5]'",
      "float,list,0.1,'[0.1, 0.1, 0.1, 1.5, 2.4, 2.4]'",
      "double,list,0.1,'0.1, 0.1, 0.1, 1.5, 2.4, 2.4",
      // Test different collection types
      "double,linkedlist,0.1,'0.1, 0.1, 0.1, 1.5, 2.4, 2.4",
  })
  public void testGetModeWithValidInputs(String numericType, String collectionType,
      double expectedMode, @ConvertWith(StringArrayConverter.class) String[] stringValues) {

    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    double actualMode = Double.parseDouble(StatisticsUtility.getMode(inputCollection).toString());
    Assertions.assertEquals(expectedMode, actualMode);
  }


  /**
   * Test calculating the median from a collection of numbers.
   *
   * @param numericType    The numeric type to test.
   * @param collectionType The collection type to test.
   * @param expectedMedian The expected median.
   * @param stringValues   The array with the string representation of the values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int,list,1.5,'[0,0,0,0,1,1,2,2,3,4,4,4]'",
      "long,list,1.5,'[0,0,0,0,1,1,2,2,3,4,4,4]'",
      "float,list,0.8,'[0.1, 0.1, 0.1, 1.5, 2.4, 2.4]'",
      "double,list,0.8,'0.1, 0.1, 0.1, 1.5, 2.4, 2.4",
      // Test different collection types
      "double,set,5,'[1,2,3,4,5,6,7,8,9]'",
      "int,linkedlist,1.5,'[0,0,0,0,1,1,2,2,3,4,4,4]'"
  })
  public void testGetMedianWithValidInputs(String numericType, String collectionType,
      double expectedMedian, @ConvertWith(StringArrayConverter.class) String[] stringValues) {

    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    Number median = StatisticsUtility.getMedian(inputCollection);
    double actualMedian = Double.parseDouble(median.toString());
    Assertions.assertEquals(expectedMedian, actualMedian);
  }

  /**
   * Test calculating the variance of a collection of numbers.
   *
   * @param numericType      The numeric type to test.
   * @param collectionType   The collection type to test.
   * @param expectedVariance The expected variance.
   * @param meanType         The mean type (arithmetic, geometric, harmonic).
   * @param stringValues     The array with the string representation of the values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int,list,6.666666666666667,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "long,list,6.666666666666667,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "float,list,7.545600000000002,ARITHMETIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      "double,list,7.545600000000002,ARITHMETIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      // Test different collection types
      "double,set,6.666666666666667,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "double,set,6.666666666666667,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      // Test with different means
      "double,list,8.480633182328768,GEOMETRIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      "double,list,10.245142176304704,HARMONIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
  })
  public void testGetVariance(String numericType, String collectionType, double expectedVariance,
      MeanType meanType, @ConvertWith(StringArrayConverter.class) String[] stringValues) {
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    double roundedVariance = StatisticsUtility.getVariance(inputCollection, meanType);
    Assertions.assertEquals(expectedVariance, roundedVariance);
  }


  /**
   * Test calculating the standard deviation of a collection of numbers.
   *
   * @param numericType               The numeric type to test.
   * @param collectionType            The collection type to test.
   * @param expectedStandardDeviation The expected standard deviation.
   * @param meanType                  The mean type (arithmetic, geometric, harmonic).
   * @param stringValues              The array with the string representation of the values to
   *                                  test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int,list,2.581988897471611,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "long,list,2.581988897471611,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "float,list,2.7469255541423037,ARITHMETIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      "double,list,2.7469255541423037,ARITHMETIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      // Test different collection types
      "double,set,2.581988897471611,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      "double,linkedlist,2.581988897471611,ARITHMETIC,'[1,2,3,4,5,6,7,8,9]'",
      // Test with different means
      "double,list,2.9121526715350567,GEOMETRIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
      "double,list,3.2008033642047904,HARMONIC,'[1.5, 2.3, 0.9, 8.3, 5.1]'",
  })
  public void testGetStandardDeviation(String numericType, String collectionType,
      double expectedStandardDeviation,
      MeanType meanType, @ConvertWith(StringArrayConverter.class) String[] stringValues) {
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    double standardDeviation = StatisticsUtility.getStandardDeviation(inputCollection, meanType);
    Assertions.assertEquals(expectedStandardDeviation, standardDeviation);
  }

  /**
   * Test getting the top most frequent elements in a collection of numbers.
   *
   * @param numericType                        The numeric type to test.
   * @param collectionType                     The collection type to test.
   * @param topNumber                          The number of elements that are more frequent.
   * @param expectedStringMostFrequentElements The array with the expected most frequent elements.
   * @param stringValues                       The array with the string representation of the
   *                                           values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int, list, 3, '[0,4,2]', '[0,0,0,0,0,1,1,2,2,2,3,4,4,4,4]'",
      "long, list, 3,'[0,4,2]', '[0,0,0,0,0,1,1,2,2,2,3,4,4,4,4]'",
      "float, list, 2, '[0.1,2.4]', '[0.1,0.1,0.1,1.5,2.4,2.4]'",
      "double, list, 2, '[0.1,2.4]', '[0.1,0.1,0.1,1.5,2.4,2.4]'",
      // Test different collection types
      "double, linkedlist, 2, '[0.1,2.4]', '[0.1,0.1,0.1,1.5,2.4,2.4]'",
  })
  public void testGetTopFrequentElements(String numericType, String collectionType, int topNumber,
      @ConvertWith(StringArrayConverter.class) String[] expectedStringMostFrequentElements,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {

    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);

    Collection<? extends Number> expectedMostFrequentElements = convertToCollection(
        expectedStringMostFrequentElements, numericType,
        collectionType);

    Collection<? extends Number> actualMostFrequentElements = StatisticsUtility
        .getTopFrequentElements(inputCollection, topNumber);
    Assertions.assertEquals(expectedMostFrequentElements, actualMostFrequentElements);
  }

  /**
   * Test retrieving the maximum value in a collection of numbers.
   *
   * @param numericType      The numeric type to test.
   * @param collectionType   The collection type to test.
   * @param expectedMaxValue The expected maximum value.
   * @param stringValues     The array with the string representation of the values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int, list, 728, '[-7,-6,0,1,2,3,4,728]'",
      "long, list, 728, '[-7,-6,0,1,2,3,4,728]'",
      "float, list, 100.1, '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "float, list, 100.12121, '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.12121]'",
      "double, list, 100.1, '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "double, list, -1.4, '[-15.7,-1.4,-10.1,-1.5,-2.4,-100.1]'",
      // Test different collection types
      "double, linkedlist, -1.4, '[-15.7,-1.4,-10.1,-1.5,-2.4,-100.1]'"
  })
  public void testGetMaxValue(String numericType, String collectionType, double expectedMaxValue,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);

    double actualMaxValue = StatisticsUtility.getMaxValue(inputCollection);
    //float does not return precise value (100.0999984741211)
    Assertions.assertEquals(expectedMaxValue, actualMaxValue);
  }

  /**
   * Test retrieving the minimum value in a collection of numbers.
   *
   * @param numericType      The numeric type to test.
   * @param collectionType   The collection type to test.
   * @param expectedMinValue The expected minimum value.
   * @param stringValues     The array with the string representation of the values to test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int, list, -56, '[-7,-6,0,1,2,3,4,-56]'",
      "long, list, -728, '[-7,-6,0,1,2,3,4,-728]'",
      "float, list, -15.7, '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "double, list, -15.7, '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      // Test different collection types
      "double, linkedlist, -15.7, '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'"
  })
  public void testGetMinValue(String numericType, String collectionType, double expectedMinValue,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    double actualMinValue = StatisticsUtility.getMinValue(inputCollection);
    Assertions.assertEquals(expectedMinValue, actualMinValue);
  }


  /**
   * Test getting the top maximum values in a collection of numbers.
   *
   * @param numericType                The numeric type to test.
   * @param collectionType             The collection type to test.
   * @param topNumber                  The number of elements that are largest.
   * @param expectedStringTopMaxValues The array with the expected largest elements.
   * @param stringValues               The array with the string representation of the values to
   *                                   test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int, list, 3, '[728,4,3]', '[-7,-6,0,1,2,3,4,728]'",
      "long, list, 3, '[728,4,3]', '[-7,-6,0,1,2,3,4,728]'",
      "float, list, 4, '[100.1,2.4,1.5,0.1]', '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "double, list, 4, '[100.1,2.4,1.5,0.1]', '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "double, list, 5, '[-1.4,-1.5,-2.4,-10.1,-15.7]', '[-15.7,-1.4,-10.1,-1.5,-2.4,-100.1]'",
      // Test different collection types
      "double, linkedlist, 5, '[-1.4,-1.5,-2.4,-10.1,-15.7]', '[-15.7,-1.4,-10.1,-1.5,-2.4,-100.1]'"
  })
  public void testGetTopMaxValue(String numericType, String collectionType, int topNumber,
      @ConvertWith(StringArrayConverter.class) String[] expectedStringTopMaxValues,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    Collection<? extends Number> expectedMostFrequentElements = convertToCollection(
        expectedStringTopMaxValues, numericType,
        collectionType);

    Collection<? extends Number> actualMostFrequentElements = StatisticsUtility
        .getTopMaxValues(inputCollection, topNumber);
    Assertions.assertEquals(expectedMostFrequentElements, actualMostFrequentElements);
  }

  /**
   * Test getting the top minimum values in a collection of numbers.
   *
   * @param numericType                The numeric type to test.
   * @param collectionType             The collection type to test.
   * @param topNumber                  The number of elements that are the smallest.
   * @param expectedStringTopMaxValues The array with the expected smallest elements.
   * @param stringValues               The array with the string representation of the values to
   *                                   test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int, list, 3, '[-7,-6,0]', '[-7,-6,0,1,2,3,4,728]'",
      "long, list, 3, '[-7,-6,0]', '[-7,-6,0,1,2,3,4,728]'",
      "float, list, 4, '[-15.7,-10.1,-1.4,0.1]', '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "double, list, 4, '[-15.7,-10.1,-1.4,0.1]', '[-15.7,-1.4,-10.1,0.1,1.5,2.4,100.1]'",
      "double, list, 3, '[-100.1,-15.7,-10.1]', '[-15.7,-1.4,-10.1,-1.5,-2.4,-100.1]'",
      // Test different collection types
      "double, linkedlist, 3, '[-100.1,-15.7,-10.1]', '[-15.7,-1.4,-10.1,-1.5,-2.4,-100.1]'"
  })
  public void testGetTopMinValue(String numericType, String collectionType, int topNumber,
      @ConvertWith(StringArrayConverter.class) String[] expectedStringTopMaxValues,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    Collection<? extends Number> expectedMostFrequentElements = convertToCollection(
        expectedStringTopMaxValues, numericType,
        collectionType);

    Collection<? extends Number> actualMostFrequentElements = StatisticsUtility
        .getTopMinValues(inputCollection, topNumber);
    Assertions.assertEquals(expectedMostFrequentElements, actualMostFrequentElements);
  }


  /**
   * Test getting the frequency distribution of all values in a collection.
   *
   * @param numericType               The numeric type to test.
   * @param collectionType            The collection type to test.
   * @param expectedElements          The unique expected elements in the input collection.
   * @param expectedElementsFrequency The expected frequency of each element in the input
   *                                  collection.
   * @param stringValues              The array with the string representation of the values to
   *                                  test.
   */
  @ParameterizedTest
  @CsvSource({
      // Test different numeric types
      "int, list,'[0,1,2,3]', '[4,5,3,1]', '[0,0,0,0,1,1,1,1,1,2,2,2,3]'",
      "long, list,'[0,1,2,3]', '[4,5,3,1]', '[0,0,0,0,1,1,1,1,1,2,2,2,3]'",
      "float, list, '[1.1,2.4,2.5,10.0,11.1]', '[2,1,2,1,1]', '[1.1,1.1,2.4,2.5,2.5,10.0,11.1]'",
      "double, list, '[1.1,2.4,2.5,10.0,11.1]', '[2,1,2,1,1]', '[1.1,1.1,2.4,2.5,2.5,10.0,11.1]'",
      // Test different collection types
      "double, linkedlist, '[1.1,2.4,2.5,10.0,11.1]', '[2,1,2,1,1]', "
          + "'[1.1,1.1,2.4,2.5,2.5,10.0,11.1]'"
  })
  public void testGetFrequency(String numericType, String collectionType,
      @ConvertWith(StringArrayConverter.class) String[] expectedElements,
      @ConvertWith(StringArrayConverter.class) String[] expectedElementsFrequency,
      @ConvertWith(StringArrayConverter.class) String[] stringValues) {

    List<? extends Number> expectedKeys = (List<? extends Number>) convertToCollection(
        expectedElements, numericType,
        "list");
    List<? extends Number> expectedValues = (List<? extends Number>) convertToCollection(
        expectedElementsFrequency, "long",
        "list");
    Collection<? extends Number> inputCollection = convertToCollection(stringValues, numericType,
        collectionType);
    Map<? extends Number, Long> actualFrequencies = StatisticsUtility
        .getFrequencyDistribution(inputCollection);
    for (Number element : actualFrequencies.keySet()) {
      long actualFrequency = actualFrequencies.get(element);
      int index = expectedKeys.indexOf(element);
      Assertions.assertEquals(expectedValues.get(index), actualFrequency);
    }
  }


  /**
   * Test calculating the mean with invalid input.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param meanType               The mean type to provide to the tested method.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false, ARITHMETIC, NullPointerException.class",
      "true, DONOTUSE, IllegalArgumentException.class",
      "true,,NullPointerException.class"
  })
  public void testGetMeanWithInvalidInput(
      boolean validCollection,
      MeanType meanType,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;

    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getMean(inputCollection, meanType));
  }

  /**
   * Test calculating the mode with invalid input.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,NullPointerException.class"
  })
  public void testGetModeWithInvalidInput(
      boolean validCollection,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getMode(inputCollection));
  }

  /**
   * Test calculating the median with invalid input.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,NullPointerException.class"
  })
  public void testGetMedianWithInvalidInput(
      boolean validCollection,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getMedian(inputCollection));
  }

  /**
   * Test calculating the variance with invalid inputs.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param meanType               The mean type to provide to the tested method.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false, ARITHMETIC, NullPointerException.class",
      "true, DONOTUSE, IllegalArgumentException.class",
      "true,,NullPointerException.class"
  })
  public void testGetVarianceWithInvalidInput(
      boolean validCollection,
      MeanType meanType,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getVariance(inputCollection, meanType));
  }

  /**
   * Test calculating the standard deviation with invalid inputs.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param meanType               The mean type to provide to the tested method.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false, ARITHMETIC, NullPointerException.class",
      "true, DONOTUSE, IllegalArgumentException.class",
      "true,,NullPointerException.class"
  })
  public void testGetStandardDeviationWithInvalidInput(
      boolean validCollection,
      MeanType meanType,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getStandardDeviation(inputCollection, meanType));
  }

  /**
   * Test retrieving the frequency distribution with invalid input.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,NullPointerException.class"
  })
  public void testGetFrequencyDistributionWithInvalidInput(
      boolean validCollection,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getFrequencyDistribution(inputCollection));
  }

  /**
   * Test getting the top most frequent elements with invalid inputs.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param topNumbers             The topNumbers to provide to the tested method.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,1,NullPointerException.class",
      "true,-1,IllegalArgumentException.class"
  })
  public void testGetTopFrequentElementsWithInvalidInput(
      boolean validCollection,
      int topNumbers,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getTopFrequentElements(inputCollection, topNumbers));
  }

  /**
   * Test getting the maximum value with invalid input.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,NullPointerException.class"
  })
  public void testGetMaxWithInvalidInput(
      boolean validCollection,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getMaxValue(inputCollection));
  }

  /**
   * Test getting the minimum value with invalid input.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,NullPointerException.class"
  })
  public void testGetMinWithInvalidInput(
      boolean validCollection,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getMinValue(inputCollection));
  }

  /**
   * Test getting the top maximum values with invalid inputs.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param topNumbers             The topNumbers to provide to the tested method.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,1,NullPointerException.class",
      "true,-1,IllegalArgumentException.class"
  })
  public void testGetTopMaxElementsWithInvalidInput(
      boolean validCollection,
      int topNumbers,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getTopMaxValues(inputCollection, topNumbers));
  }

  /**
   * Test getting the top minimum values with invalid inputs.
   *
   * @param validCollection        A flag for the input collection to provide to the tested method.
   *                               If true, provides an empty arraylist and null otherwise.
   * @param topNumbers             The topNumbers to provide to the tested method.
   * @param expectedExceptionClass The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      "false,1,NullPointerException.class",
      "true,-1,IllegalArgumentException.class"
  })
  public void testGetTopMinElementsWithInvalidInput(
      boolean validCollection,
      int topNumbers,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {

    List<Integer> inputCollection = validCollection ? new ArrayList<>() : null;
    Assertions.assertThrows(expectedExceptionClass,
        () -> StatisticsUtility.getTopMinValues(inputCollection, topNumbers));
  }
}

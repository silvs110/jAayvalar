package org.padaiyal.utilities.aayvalar.statistics;


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.padaiyal.utilities.I18nUtility;

/**
 * Statistics utility library for retrieving basic statistical metrics.
 */
public class StatisticsUtility {

  static {
    I18nUtility.addResourceBundle(
        StatisticsUtility.class,
        StatisticsUtility.class.getSimpleName(),
        Locale.US
    );
  }

  /**
   * Empty private constructor as this utility class is not meant to be used as an instance.
   */
  private StatisticsUtility() {
  }

  /**
   * Gets the mean (arithmetic, geometric or harmonic) of the provided collection. For more
   * information about the different types of means: https://en.wikipedia.org/wiki/Mean#Types_of_means
   *
   * @param inputCollection The collection to get the mean from.
   * @param meanType        The type of mean to calculate.
   * @return The mean of the values inside a collection.
   */
  public static double getMean(Collection<? extends Number> inputCollection, MeanType meanType) {
    Objects.requireNonNull(inputCollection);

    double collectionLength = inputCollection.size();
    return switch (meanType) {
      case ARITHMETIC -> //noinspection OptionalGetWithoutIsPresent
          inputCollection.parallelStream()
              .mapToDouble(element -> Double.parseDouble(element.toString()))
              .average()
              .getAsDouble();
      case GEOMETRIC -> Math.pow(
          inputCollection.parallelStream()
              .mapToDouble(element -> Double.parseDouble(element.toString()))
              .reduce(1, (subtotal, element) -> subtotal * element),
          1 / collectionLength);
      case HARMONIC -> collectionLength / (
          inputCollection.parallelStream()
              .mapToDouble(element -> Double.parseDouble(element.toString()))
              .map(element -> 1 / element)
              .sum());
      default -> throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "StatisticsUtility.error.invalidMeanType",
              meanType.toString()
          )
      );
    };
  }

  /**
   * Gets the median of the provided collection. The median is the value in the middle of a sorted
   * collection. For more information: https://en.wikipedia.org/wiki/Median
   *
   * @param inputCollection The collection to get the median from.
   * @return The median from the provided collection.
   */
  public static double getMedian(Collection<? extends Number> inputCollection) {
    Objects.requireNonNull(inputCollection);

    int collectionLength = inputCollection.size();
    //noinspection OptionalGetWithoutIsPresent
    return inputCollection.parallelStream()
        .mapToDouble(element -> Double.parseDouble(element.toString()))
        .sorted()
        .skip((collectionLength - 1) / 2)
        .limit(2 - collectionLength % 2)
        .average()
        .getAsDouble();

  }

  /**
   * Get the mode from the provided collection.
   * Mode is the most frequent token in a collection of tokens.
   * For more information: https://en.wikipedia.org/wiki/Mode
   *
   * @param inputCollection The collection to retrieve the mode from.
   * @return The mode from the provided collection.
   */
  public static <T> T getMode(Collection<T> inputCollection) {
    return getTopFrequentElements(inputCollection, 1).get(0);
  }

  /**
   * Get the frequency distribution of elements from the provided collection.
   * Frequency distribution is a graph that displays the frequency of a collection.
   * For more information: https://en.wikipedia.org/wiki/Frequency_distribution
   *
   * @param inputCollection The collection to retrieve its elements frequency.
   * @return The frequency distribution of the elements in the provided collection.
   */
  public static <T> Map<T, Long> getFrequencyDistribution(Collection<T> inputCollection) {
    Objects.requireNonNull(inputCollection);
    return inputCollection.parallelStream()
        .collect(
            Collectors.groupingBy(
                token -> token,
                Collectors.counting()
            )
        );
  }

  /**
   * Gets the top frequent numbers in the provided collection.
   *
   * @param inputCollection The provided collection to get the top most frequent
   * @param topNumbers      The number of most frequent values.
   * @return The top most frequent numbers in the input collection.
   */
  public static <T> List<T> getTopFrequentElements(Collection<T> inputCollection, int topNumbers) {
    Objects.requireNonNull(inputCollection);
    if (topNumbers < 1) {
      throw new IllegalArgumentException(
          I18nUtility.getString("StatisticsUtility.error.negativeTopNumberIsProvided")
      );
    }

    return getFrequencyDistribution(inputCollection).entrySet()
        .parallelStream()
        .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(topNumbers)
        .map(Entry::getKey)
        .collect(Collectors.toList());
  }

  /**
   * Get SummaryStatistics from the provided collection.
   *
   * @param inputCollection The input collection to retrieve the SummaryStatistics from.
   * @return The SummaryStatistics from the provided collection.
   */
  public static DoubleSummaryStatistics getSummaryStatistics(
      Collection<? extends Number> inputCollection) {
    return inputCollection.parallelStream()
        .mapToDouble(element -> Double.parseDouble(element.toString()))
        .summaryStatistics();
  }

  /**
   * Get the maximum value from the input collection.
   *
   * @param inputCollection The input collection to retrieve the maximum value from.
   * @return The maximum value from the input collection.
   */
  public static double getMaxValue(Collection<? extends Number> inputCollection) {
    return getSummaryStatistics(inputCollection).getMax();
  }

  /**
   * Get the minimum value from the input collection.
   *
   * @param inputCollection The input collection to retrieve the minimum value from.
   * @return The minimum value from the input collection.
   */
  public static double getMinValue(Collection<? extends Number> inputCollection) {
    return getSummaryStatistics(inputCollection).getMin();
  }

  /**
   * Get top maximum values.
   *
   * @param inputCollection The input collection to retrieve the top maximum values.
   * @param topNumbers      The top numbers of maximum values.
   * @return The top maximum values.
   */
  public static List<? extends Number> getTopMaxValues(Collection<? extends Number> inputCollection,
      int topNumbers) {

    if (topNumbers < 1) {
      throw new IllegalArgumentException(
          I18nUtility.getString("StatisticsUtility.error.negativeTopNumberIsProvided")
      );
    }
    return inputCollection.parallelStream()
        .sorted(Collections.reverseOrder())
        .limit(topNumbers)
        .collect(Collectors.toList());
  }

  /**
   * Get top minimum values.
   *
   * @param inputCollection The input collection to retrieve the top minimum values.
   * @param topNumbers      The top numbers of minimum values.
   * @return The top minimum values.
   */
  public static List<? extends Number> getTopMinValues(Collection<? extends Number> inputCollection,
      int topNumbers) {

    if (topNumbers < 1) {
      throw new IllegalArgumentException(
          I18nUtility.getString("StatisticsUtility.error.negativeTopNumberIsProvided")
      );
    }

    return inputCollection.parallelStream()
        .sorted()
        .limit(topNumbers)
        .collect(Collectors.toList());
  }

  /**
   * Gets the variance from the provided collection.
   * The variance is the measure of variability in a collection of numbers.
   * For more information: https://en.wikipedia.org/wiki/Variance
   *
   * @param inputCollection The input collection to retrieve variance.
   * @param meanType        The mean type to use for calculation.
   * @return The variance from the provided collection.
   */
  public static double getVariance(Collection<? extends Number> inputCollection,
      MeanType meanType) {
    double mean = getMean(inputCollection, meanType);
    //noinspection OptionalGetWithoutIsPresent
    return inputCollection.parallelStream()
        .mapToDouble(element -> Double.parseDouble(element.toString()))
        .map(value -> Math.pow((value - mean), 2))
        .average()
        .getAsDouble();
  }

  /**
   * Gets the standard deviation from the provided collection.
   * The standard deviation is the measurement of variation of collection of values.
   *
   * @param inputCollection The input collection to retrieve the standard deviation.
   * @param meanType        The mean type to use for calculation.
   * @return The standard deviation from the provided collection.
   */
  public static double getStandardDeviation(Collection<? extends Number> inputCollection,
      MeanType meanType) {
    return Math.sqrt(getVariance(inputCollection, meanType));
  }
}

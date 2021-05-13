package org.padaiyal.utilities.aayvalar.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.statistics.StatisticsUtility;

@SuppressWarnings("SpellCheckingInspection")
public class StringUtility {

  static {
    I18nUtility.addResourceBundle(
        StringUtility.class,
        StringUtility.class.getSimpleName(),
        Locale.US
    );
  }

  /**
   * Repeats the given string the specified number of times and returns it as a single string
   * output.
   *
   * @param str         String too repeat
   * @param repeatCount Number of times to repeat str
   * @return The string with repeatCount contiguous occurrences of str.
   * @deprecated use {@link String#repeat(int)} instead.
   */
  @Deprecated
  public static String repeat(String str, long repeatCount) {
    Objects.requireNonNull(str);
    if (repeatCount < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "utilities.StringUtility.repeatString.error.negativeRepeatCount",
              repeatCount
          )
      );
    }
    StringBuilder stringBuilder = new StringBuilder();
    LongStream.range(0, repeatCount)
        .parallel()
        .forEach(index -> stringBuilder.append(str));
    return stringBuilder.toString();
  }

  /**
   * Given a string, it computes the character frequency distribution.
   *
   * @param str String input
   * @return Character frequency distribution
   */
  public static Map<Character, Long> getCharacterFrequencyDistribution(String str) {
    Objects.requireNonNull(str);

    return StatisticsUtility.getFrequencyDistribution(
        str.chars()
            .mapToObj(ch -> (char) ch)
            .collect(Collectors.toList())
    );
  }

  public static Map<String, Long> getWordFrequencyDistribution(String str, String... delimiters) {
    Objects.requireNonNull(str);
    Objects.requireNonNull(delimiters);
    Arrays.stream(delimiters)
        .forEach(Objects::requireNonNull);

    if (delimiters.length == 0) {
      throw new IllegalArgumentException(I18nUtility.getString(
          "utilities.StringUtility.getWordFrequencyDistribution.error.noDelimitersSpecified"));
    }

    String delimiterRegex;
    delimiterRegex = Arrays.stream(delimiters)
        .map(delimiter -> String.format("(%s)", delimiter))
        .reduce((d1, d2) -> d1 + "|" + d2)
        .get();
    List<String> words = Arrays.asList(str.split(delimiterRegex));

    return StatisticsUtility.getFrequencyDistribution(words);
  }

  public static double getStringSimilarityDistance(
      StringSimilarityDistanceType stringSimilarityDistanceType, String str1, String str2) {
    Objects.requireNonNull(stringSimilarityDistanceType);
    Objects.requireNonNull(str1);
    Objects.requireNonNull(str2);

    return switch (stringSimilarityDistanceType) {
      case HAMMING -> getHammingDistance(str1, str2);
      case LEVENSHTEIN -> getLevenshteinDistance(str1, str2);
      case JARO -> getJaroDistance(str1, str2);
      case JARO_WRINKLER -> getJaroWrinklerDistance(str1, str2, 0.1);
      case JACCARD_INDEX -> getJaccardIndexDistance(str1, str2, new String[]{" "});
      case SORENSEN_DICE -> getSorensenDiceDistance(str1, str2, new String[]{" "});
      default -> throw new UnsupportedOperationException(
          I18nUtility
              .getFormattedString(
                  "utilities.StringUtility.getWordFrequencyDistribution.error.distance.invalid",
                  stringSimilarityDistanceType
              )
      );
    };
  }

  /**
   * Computes the hamming distance given two strings str1 and str2, based on the following formula.
   * ┌ |   min(len(str1), len(str2))               if min(len(str1), len(str2)) = 0 | |  ┌ |  |
   * hamm( |  |     substr(str1, 1, len(str1)), |  |     substr(str2, 1, len(str2)) otherwise
   * hamm(str1, str2) =  |  |  ) | -|                + |  |  ┌ |  |  |   1   if str1(0) ≠ str2(0) |
   * | -| |  |  |   0   otherwise |  |  └ |  └ └
   *
   * @param str1 String input
   * @param str2 String input
   * @return The hamming distance between the two input strings
   */
  private static double getHammingDistance(String str1, String str2) {
    int hammingDistance = Math.abs(str1.length() - str2.length());
    hammingDistance += IntStream.range(0, Math.min(str1.length(), str2.length()))
        .filter(index -> str1.charAt(index) != str2.charAt(index))
        .count();
    return hammingDistance;
  }

  /**
   * Computes the levenshtein distance given two strings str1 and str2, by first computing a
   * distance matrix based on the following formula:
   * ┌ |  ┌ |  | lev(str1_index, str2_index - 1) + 1     if str2_index > 0 | -| |  | ∞ otherwise |
   * └ | |  ┌ |  | lev(str1_index - 1, str2_index) + 1     if str1_index > 0 | -| |  | ∞
   *                           otherwise |  └ |  ┌ |  |   ┌ lev(str1_index, str2_index) = min - |  |
   *   | lev(str1_index - 1, str2_index - 1)     if str1_index > 0 |  |  -|
   *                and str2_index > 0 |  |   | | |   | 0                                       if
   * str1_index = 0 |  |   | and str2_index = 0 |  |   | |  |   | ∞ otherwise |  |   └ | -|
   *               + |  |   ┌ |  |   | 1     if str1(str1_index) ≠ str2(str2_index) |  |  -| |  |
   * | 0     otherwise |  |   └ |  └ └
   * Once the levenshtein distance matrix is computed, the last value in the matrix is the
   * Levenshtein distance between the two provided strings.
   *
   * @param str1 String input
   * @param str2 String input
   * @return The levenshtein distance between the two input strings
   */
  private static double getLevenshteinDistance(String str1, String str2) {
    int[][] distanceMatrix = new int[str1.length()][str2.length()];
    int diagonalDistance;
    int verticalDistance;
    int horizontalDistance;
    for (int i = 0; i < str1.length(); i++) {
      for (int j = 0; j < str2.length(); j++) {
        diagonalDistance = verticalDistance = horizontalDistance = Integer.MAX_VALUE;
        int characterMatchCount = (str1.charAt(i) == str2.charAt(j)) ? 0 : 1;
        if (i > 0 && j > 0) {
          diagonalDistance = distanceMatrix[i - 1][j - 1] + characterMatchCount;
        } else if (i == 0 && j == 0) {
          diagonalDistance = characterMatchCount;
        }
        if (i > 0) {
          verticalDistance = distanceMatrix[i - 1][j] + 1;
        }
        if (j > 0) {
          horizontalDistance = distanceMatrix[i][j - 1] + 1;
        }
        distanceMatrix[i][j] = Math
            .min(Math.min(diagonalDistance, verticalDistance), horizontalDistance);
      }
    }
    for (int[] row : distanceMatrix) {
      System.out.println(Arrays.toString(row));
    }
    return distanceMatrix[str1.length() - 1][str2.length() - 1];
  }

  /**
   * Computes the Jaro distance between the two provided strings. ┌ |  0       if m=0 jaro(str1,
   * str2) -| |  (1/3) * ((m/(len(str1))) + (m/(len(str2))) + (m-t)/m)   otherwise └ Where m is the
   * number of matching character between str1 and str2. Two characters from str1 and str2 are
   * considered to match only if the difference between their indexes are <= floor((max(len(str1),
   * len(str2))/2) - 1). For counting m, only 1:1 matches are considered, the first matching
   * unmatched character pair across both strings.
   * <p>t is the number of transpositions i.e. the number of matches in which if the matching
   * indices were transposed between the two strings (Only when the indexes in both the strings
   * aren't same), they still match. For example:
   * str1   LASTINGZZZZZ 0123456789AB str2   LTSALLLAAAAA<p>
   * str1[0] and str2[0] are a valid match, but no transpositions as the indexes are the same.<p>
   * str1[1] and str2[3] are a valid match and a valid transposition as str1[3] and str2[1] also
   * match.
   *
   * @param str1 String input
   * @param str2 String input
   * @return The Jaro distance between the two input strings
   */
  private static double getJaroDistance(String str1, String str2) {
    Objects.requireNonNull(str1);
    Objects.requireNonNull(str2);

    int matchingDistance = Math.max(str1.length(), str2.length()) / 2 - 1;
    HashMap<Integer, Boolean> indexesMatchedInStr2 = new HashMap<>();
    int numberOfMatchingCharacters = IntStream.range(0, str1.length())
        //.map(index -> { System.out.println("Index:" + index);return index;})
        .map(
            index -> IntStream.range(
                Math.max(0, index - matchingDistance),
                Math.min(
                    index + matchingDistance + 1,
                    Math.min(str1.length(), str2.length())
                )
            )
                .filter(
                    windowIndex -> {
                      if (!indexesMatchedInStr2.containsKey(windowIndex)
                          && str2.charAt(windowIndex) == str1.charAt(index)) {
                        indexesMatchedInStr2.put(
                            windowIndex,
                            (windowIndex < str1.length())
                                && (index < str2.length())
                                && index != windowIndex
                                && str2.charAt(index) == str1.charAt(windowIndex)
                        );
                        return true;
                      }
                      return false;
                    }
                )
                .findFirst()
                .isPresent() ? 1 : 0
        )
        .sum();
    int numberOfTranspositions = (int) indexesMatchedInStr2.values()
        .stream()
        .filter(doesTransposeMatch -> doesTransposeMatch)
        .count();

    double jaroSimilarity = 0.0D;

    if (numberOfMatchingCharacters != 0) {
      jaroSimilarity = (1.0 / 3.0)
          * (
          (numberOfMatchingCharacters / (double) str1.length())
              + (numberOfMatchingCharacters / (double) str2.length())
              + (1 - (numberOfTranspositions / 2.0) / (double) numberOfMatchingCharacters)
        );
    }

    return jaroSimilarity;
  }

  /**
   * Identify the longest matching prefix given two string inputs.
   *
   * @param str1 String input 1
   * @param str2 String input 2
   * @return Longest matching prefix
   */
  private static Integer getLongestMatchingPrefixLength(String str1, String str2) {
    int longestMatchingPrefixLength = 0;
    for (int i = 0; i < Math.min(str1.length(), str2.length()); i++) {
      if (str1.charAt(i) == str2.charAt(i)) {
        longestMatchingPrefixLength++;
      } else {
        break;
      }
    }
    return longestMatchingPrefixLength;
  }

  /**
   * Computes the Jaro Wrinkler distance between the two provided strings.
   * <p>jaro_wrinkler(str1, str2) = jaro(str1, str2) + (l * p * (1 - jaro(str1, str2)))</p>
   * Where l is the length of the common matching prefix b/w str1 & str2. p is a constant scaling
   * factor to adjust the weightage of the common prefix. A standard value for p is 0.1 .
   *
   * @param str1 String input
   * @param str2 String input
   * @return The Jaro Wrinkler distance between the two input strings
   */
  private static double getJaroWrinklerDistance(String str1, String str2,
      @SuppressWarnings("SameParameterValue") double scalingFactor) {
    Objects.requireNonNull(str1);
    Objects.requireNonNull(str2);

    double jaroDistance = getJaroDistance(str1, str2);

    return jaroDistance + getLongestMatchingPrefixLength(str1, str2) * scalingFactor * (1
        - jaroDistance);
  }

  /**
   * Finds the number of common tokens given two frequency distribution maps.
   *
   * @param str1Tokens Frequency distribution of tokens present in str1.
   * @param str2Tokens Frequency distribution of tokens present in str2.
   * @return The number of common tokens given two frequency distribution maps
   */
  private static long getCommonTokensCount(Map<String, Long> str1Tokens,
      Map<String, Long> str2Tokens) {
    return str1Tokens.keySet()
        .stream()
        .filter(str2Tokens::containsKey)
        .mapToLong(commonToken -> Math.min(
            str1Tokens.get(commonToken),
            str2Tokens.get(commonToken)
            )
        )
        .sum();
  }

  /**
   * Computes the Jaccard Index distance between the two provided strings. Given two sets of tokens
   * A & B, the Jaccard Index Distance is computed as follows: jaccard_index(A,B) = (|A⋂B|)/(|A⋃B|)
   * Where |..| denotes the number of elements in the resultant set.
   *
   * @param str1 String input
   * @param str2 String input
   * @return The Jaccard Index distance between the two input strings
   */
  @SuppressWarnings("Duplicates")
  private static double getJaccardIndexDistance(String str1, String str2, String[] delimiters) {
    Objects.requireNonNull(str1);
    Objects.requireNonNull(str2);
    Objects.requireNonNull(delimiters);

    Map<String, Long> str1Tokens = getWordFrequencyDistribution(str1, delimiters);
    Map<String, Long> str2Tokens = getWordFrequencyDistribution(str2, delimiters);
    long commonTokensCount = getCommonTokensCount(str1Tokens, str2Tokens);

    Set<String> totalUniqueTokens = new HashSet<>(str1Tokens.keySet());
    totalUniqueTokens.addAll(str2Tokens.keySet());
    int totalUniqueTokensCount = totalUniqueTokens.size();

    return commonTokensCount / (double) totalUniqueTokensCount;
  }

  private static Map<String, Long> getLongestCommonSubSequencesHelperFunction(
      String str1,
      String str2,
      @SuppressWarnings("SameParameterValue")
          boolean storeOnlyOneSubsequence
  ) {
    int[] temp = new int[str2.length()];
    int maxLcsLength = 0;
    List<Integer> lcsStartIndexesInStr2 = new ArrayList<>();
    boolean doCharactersMatch;
    for (int str1Index = str1.length() - 1; str1Index >= 0; str1Index--) {
      for (int str2Index = 0; str2Index < str2.length(); str2Index++) {
        doCharactersMatch = (str1.charAt(str1Index) == str2.charAt(str2Index));
        int additiveTerm = 0;
        if (str2Index < str2.length() - 1 && doCharactersMatch) {
          additiveTerm = temp[str2Index + 1];
        }

        temp[str2Index] = additiveTerm + (doCharactersMatch ? 1 : 0);
        if (temp[str2Index] > maxLcsLength) {
          maxLcsLength = temp[str2Index];
          lcsStartIndexesInStr2.clear();
          lcsStartIndexesInStr2.add(str2Index);
        } else if (temp[str2Index] == maxLcsLength && (lcsStartIndexesInStr2.size() == 0
            || !storeOnlyOneSubsequence)) {
          lcsStartIndexesInStr2.add(str2Index);
        }
      }
    }

    int finalMaxLcsLength = maxLcsLength;
    return StatisticsUtility.getFrequencyDistribution(
        lcsStartIndexesInStr2.stream()
            .map(index -> str2.substring(index, index + finalMaxLcsLength))
            .collect(Collectors.toList())
    );
  }

  /**
   * Given 2 strings, computes the length of the longest common sub-sequence.
   *
   * @param str1 String input 1
   * @param str2 String input 2
   * @return Length of the longest common sub-sequence
   */
  public static Integer getLongestCommonSubSequenceLength(String str1, String str2) {
    Objects.requireNonNull(str1);
    Objects.requireNonNull(str2);

    return getLongestCommonSubSequencesHelperFunction(str1, str2, true)
        .keySet()
        .stream()
        .map(String::length)
        .findFirst()
        .orElse(0);
  }

  /**
   * Given 2 string inputs, computes the Sorensen Dice distance.
   *
   * @param str1       String input 1
   * @param str2       String input 2
   * @param delimiters Delimiters used to separate words
   * @return The Sorensen Dice distance between the two provided inputs
   */
  @SuppressWarnings("Duplicates")
  public static double getSorensenDiceDistance(String str1, String str2, String[] delimiters) {
    Objects.requireNonNull(str1);
    Objects.requireNonNull(str2);
    Objects.requireNonNull(delimiters);

    Map<String, Long> str1Tokens = getWordFrequencyDistribution(str1, delimiters);
    Map<String, Long> str2Tokens = getWordFrequencyDistribution(str2, delimiters);
    long commonTokensCount = getCommonTokensCount(str1Tokens, str2Tokens);
    return 2.0 * commonTokensCount / (double) (str1Tokens.size() + str2Tokens.size());
  }

  public enum StringSimilarityDistanceType {
    HAMMING,
    LEVENSHTEIN,
    JARO,
    JARO_WRINKLER,
    JACCARD_INDEX,
    SORENSEN_DICE,
    UNKNOWN
  }
}


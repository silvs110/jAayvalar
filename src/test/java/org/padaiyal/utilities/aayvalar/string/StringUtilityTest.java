package org.padaiyal.utilities.aayvalar.string;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.testutils.ExceptionClassConverter;
import org.padaiyal.utilities.aayvalar.testutils.StringArrayConverter;

@SuppressWarnings("SpellCheckingInspection")
class StringUtilityTest {

  public void testExecutable(Executable executable, Class<Exception> expectedException) {
    if (expectedException != null) {
      Assertions.assertThrows(
          expectedException,
          executable
      );
    } else {
      Assertions.assertDoesNotThrow(executable);
    }
  }

  /**
   * Test repeatString functionality with positive, negative, 0 repeat count and null string value.
   *
   * @param string         String to repeat.
   * @param repeatCount    Number of times too repeat.
   * @param expectedOutput Expected output.
   */
  @ParameterizedTest
  @CsvSource(
      {
          "lol, 5, lollollollollol,",
          "lol, 0, '',",
          "lol, -1, '', IllegalArgumentException.class",
          "'', 10000, '',",
          ", 100, '', NullPointerException.class"
      }
  )
  void testRepeatString(
      String string,
      long repeatCount,
      String expectedOutput,
      @ConvertWith(ExceptionClassConverter.class)
          Class<Exception> expectedException
  ) {
    Executable executable = () -> {
      @SuppressWarnings("deprecation")
      String actualOutput = StringUtility.repeat(string, repeatCount);
      Assertions.assertEquals(expectedOutput, actualOutput);
    };
    testExecutable(executable, expectedException);
  }

  @ParameterizedTest
  @CsvSource(
      {
          "a b a c a b c, '{\" \":6, \"a\":3, \"b\":2, \"c\":2}',",
          ",, NullPointerException.class"
      }
  )
  void testGetCharacterFrequencyDistribution(
      String str,
      String expectedOutput,
      @ConvertWith(ExceptionClassConverter.class)
          Class<Exception> expectedException
  ) {

    Executable executable = () -> {

      Map<Character, Long> actualOutputMap = StringUtility.getCharacterFrequencyDistribution(
          str
      );
      Map expectedOutputMap = new ObjectMapper()
          .readValue(expectedOutput, HashMap.class);
      expectedOutputMap.forEach((key, value) -> Assertions.assertEquals(
          Long.parseLong(value.toString()),
          actualOutputMap.get(
              key.toString()
                  .charAt(0)
          )
      ));
    };
    testExecutable(executable, expectedException);
  }

  @ParameterizedTest
  @CsvSource(
      {
          "a b a c a b c, '[ ,b]', '{\"\":4, \"a\":3, \"c\":2}',",
          "a b a|c a|b c, '[]',, IllegalArgumentException.class",
          "a b a|c a|b c,,, NullPointerException.class",
          ",'[,]',, NullPointerException.class"
      }
  )
  void testGetWordFrequencyDistribution(
      String str,
      @ConvertWith(StringArrayConverter.class)
          String[] delimiters,
      String expectedOutput,
      @ConvertWith(ExceptionClassConverter.class)
          Class<Exception> expectedException
  ) {
    // TODO: Add assertion statement and more test cases
    //        Assertions.assertEquals();

    Executable executable = () -> {

      Map<String, Long> actualOutputMap = StringUtility.getWordFrequencyDistribution(
          str,
          delimiters
      );
      Map expectedOutputMap = new ObjectMapper()
          .readValue(expectedOutput, HashMap.class);
      expectedOutputMap.forEach((key, value) -> Assertions.assertEquals(
          Long.parseLong(value.toString()),
          actualOutputMap.get(key)
      ));
    };
    testExecutable(executable, expectedException);
  }

  /**
   * TODO: Ensure all similarity distances are tested.
   */
  @ParameterizedTest
  @CsvSource(
      {
          "HAMMING, HELLO WORLD, WORLD HELLO, 8,",
          "HAMMING, WISDOM COMES WITH MATURITY, FREEDOM COMES WITHE WISDOM, 26,",
          "JACCARD_INDEX, HELLO WORLD, WORLD HELLO, 1.0,",
          "JACCARD_INDEX, HELLO WORLD, HELLO NEW WORLD, 0.6666666666666666,",
          "JARO, TUNPS, TUNSP, 0.9333333333333332,",
          "JARO, TUNSPS, TUNSEP, 0.888888888888889,",
          "JARO, SASASASASASAS, ASASASASASASA, 0.782051282051282,",
          "JARO_WRINKLER, DwAyNE, DuANE, 0.84,",
          "JARO_WRINKLER, TRACE, TRATE, 0.9066666666666667,",
          "LEVENSHTEIN,   HONDA,      HYUNDAI,    3,",
          "LEVENSHTEIN,   KITTEN,     SITTING,    3,",
          "LEVENSHTEIN,   INTENTION,  EXECUTION,  5,",
          "SORENSEN_DICE,  HELLO WORLD, WORLD HELLO, 1.0,",
          "SORENSEN_DICE,  HELLO WORLD, HELLO NEW WORLD, 0.8,",
          "UNKNOWN, HELLO WORLD, HELLO OLD WORLD, 0.0, UnsupportedOperationException.class",

          // Null inputs
          ",,,, NullPointerException.class",
          "HAMMING,, WORLD HELLO,, NullPointerException.class",
          "HAMMING, WISDOM COMES WITH MATURITY,,, NullPointerException.class",
          "JACCARD_INDEX,, WORLD HELLO,, NullPointerException.class",
          "JACCARD_INDEX, HELLO WORLD,,, NullPointerException.class",
          "JARO,, TUNSP,, NullPointerException.class",
          "JARO, TUNSPS,,, NullPointerException.class",
          "JARO_WRINKLER,, DuANE,, NullPointerException.class",
          "JARO_WRINKLER, TRACE,,, NullPointerException.class",
          "LEVENSHTEIN,, HYUNDAI,, NullPointerException.class",
          "LEVENSHTEIN,KITTEN,,, NullPointerException.class",
          "SORENSEN_DICE,, WORLD HELLO,, NullPointerException.class",
          "SORENSEN_DICE, HELLO WORLD,,, NullPointerException.class",
          "UNKNOWN,, HELLO OLD WORLD,, NullPointerException.class, NullPointerException.class",
          "UNKNOWN,HELLO WORLD,,, NullPointerException.class",
      }
  )
  void testStringSimilarityDistance(
      StringUtility.StringSimilarityDistanceType stringSimilarityDistanceType,
      String str1, String str2,
      Double expectedDistance,
      @ConvertWith(ExceptionClassConverter.class)
          Class<Exception> expectedException
  ) {
    Executable executable = () -> Assertions.assertEquals(
        expectedDistance,
        StringUtility.getStringSimilarityDistance(stringSimilarityDistanceType, str1, str2)
    );
    testExecutable(executable, expectedException);
  }

  @ParameterizedTest
  @CsvSource(
      {
          "JACCARDI, LBACCARDILLO, 7,",
          "JACCARDIPPP, LBACCARDILLARDIPPPO, 7,",
          "JACCARDI, '', 0,",
          "'', LBACCARDILLO, 0,",
          "'', '', 0,",

          // Null inputs.
          "lol,,, NullPointerException.class",
          ", sub,, NullPointerException.class",
          ",,, NullPointerException.class"

      }
  )
  void testLongestCommonSubSequenceWithNullInput(
      String str1,
      String str2,
      Integer expectedOutput,
      @ConvertWith(ExceptionClassConverter.class)
          Class<Exception> expectedException
  ) {
    Executable executable = () -> {
      Assertions.assertEquals(
          expectedOutput,
          StringUtility.getLongestCommonSubSequenceLength(str1, str2)
      );
    };
    testExecutable(executable, expectedException);
  }
}

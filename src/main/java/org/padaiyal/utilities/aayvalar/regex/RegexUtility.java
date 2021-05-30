package org.padaiyal.utilities.aayvalar.regex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.PropertyUtility;
import org.padaiyal.utilities.aayvalar.regex.abstractions.Comparison;
import org.padaiyal.utilities.aayvalar.regex.exceptions.InvalidRegexException;


/**
 * Utility class for manipulating regex expressions.
 */
public class RegexUtility {

  /**
   * Logger object used to log error and information.
   */
  private static final Logger logger = LogManager.getLogger(RegexUtility.class);
  /**
   * Random object.
   */
  private static final Random randomObj = new Random();
  /**
   * Personal Identifiable Information regex expressions file name.
   */
  private static String regexFieldsPropertiesFileName;
  /**
   * Word characters in a regex expression. Equivalent to \w.
   */
  private static String regexWordCharacters;
  /**
   * Digits characters in a regex expression. Equivalent to \d.
   */
  private static String regexDigitCharacters;
  /**
   * Runnable for initializing properties files.
   */
  public static Runnable initializeDependantValue = () -> {
    I18nUtility.addResourceBundle(
        RegexUtility.class,
        RegexUtility.class.getSimpleName(),
        Locale.US
    );

    try {
      PropertyUtility.addPropertyFile(
          RegexUtility.class,
          RegexUtility.class.getSimpleName() + ".properties"
      );

      regexFieldsPropertiesFileName =
          PropertyUtility.getProperty("RegexUtility.personalIdentifiableInformationRegexFileName");
      regexWordCharacters = PropertyUtility.getProperty("RegexUtility.regex.wordCharacters");
      regexDigitCharacters = PropertyUtility.getProperty("RegexUtility.regex.digitCharacters");

      PropertyUtility.addPropertyFile(
          RegexUtility.class,
          regexFieldsPropertiesFileName
      );

    } catch (IOException e) {
      logger.error(e);
    }
  };

  static {
    initializeDependantValue.run();
  }

  /**
   * Private constructor.
   */
  private RegexUtility() {
  }

  /**
   * Checks if at least one occurrence of the regex expression can be found in the input.
   *
   * @param patternString  Pattern to match
   * @param patternOptions Pattern match flags
   * @param input          Input string
   * @return Returns true if the pattern matches the input, else false
   */
  public static boolean find(String patternString, int patternOptions, String input) {
    return Pattern.compile(patternString, patternOptions)
        .matcher(input)
        .find();
  }

  /**
   * Checks if the whole input matches the specified pattern.
   *
   * @param patternString  Pattern to match
   * @param patternOptions Pattern match flags
   * @param input          Input string
   * @return Returns true if the pattern matches the input, else false
   */
  public static boolean matches(String patternString, int patternOptions, String input) {
    return Pattern.compile(patternString, patternOptions)
        .matcher(input)
        .matches();
  }

  /**
   * Get the first limit occurrences of the regex in the input string.
   *
   * @param patternString  Pattern to match.
   * @param patternOptions Regex flags.
   * @param limit          -1 means get all matches, 0 means none.
   * @param input          Input string to find matches in.
   * @return List of matches where each match is a list of groups.
   */
  public static List<List<String>> getMatches(
      String patternString,
      int patternOptions,
      int limit,
      String input
  ) throws InvalidRegexException {

    // Ensure that the input regex is valid
    if (!isValidRegex(patternString)) {
      throw new InvalidRegexException(patternString);
    }

    List<List<String>> matches = new ArrayList<>();
    // If no matches are to be found. return empty list.
    if (limit == 0) {
      return matches;
    } else if (limit < -1) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "RegexUtility.exception.negativeLimitGetMatches",
              limit
          )
      );
    }

    Pattern patternObj = Pattern.compile(patternString, patternOptions);
    Matcher matcher = patternObj.matcher(input);

    matches = matcher.results()
        .parallel()
        .map(
            match -> IntStream.range(0, match.groupCount() + 1)
                .boxed()
                .map(match::group)
                .collect(Collectors.toList()))
        .collect(Collectors.toList());

    if (limit == -1) {
      return matches;
    }

    return matches.subList(0, Math.min(limit, matches.size()));

  }

  /**
   * Get all the occurrences of the regex in the input string.
   *
   * @param patternString  Pattern to match.
   * @param patternOptions Regex flags.
   * @param input          Input string to find matches in.
   * @return List of matches where each match is a list of groups.
   */
  public static List<List<String>> getAllMatches(String patternString, int patternOptions,
      String input) throws InvalidRegexException {
    return getMatches(patternString, patternOptions, -1, input);
  }

  /**
   * Find identifiable personal information from the input data.
   *
   * @param data Input string.
   * @return A map of identifiable personal information to their corresponding matches.
   */
  public static Map<String, List<String>> findIdentifiablePersonalInformation(String data) {

    Properties regexFields = PropertyUtility.getProperties(
        RegexUtility.class,
        regexFieldsPropertiesFileName
    );
    return findAllMatchingStringsFromRegexPropertiesFile(regexFields, data);

  }

  /**
   * Finds all substrings inside the inputString that matches any of the regex in the provided
   * Properties object.
   *
   * @param regexFields The regex expressions to match.
   * @param inputString The inputString to find the substrings that match the provided regex
   *                    expressions.
   * @return A map of the regex expression keys to their matching substring.
   */
  public static Map<String, List<String>> findAllMatchingStringsFromRegexPropertiesFile(
      Properties regexFields,
      String inputString
  ) {
    return regexFields.entrySet()
        .parallelStream()
        .filter(
            entry -> {
              String regexPattern = entry.getValue().toString();
              if (!regexPattern.equals("")) {
                return find(regexPattern, 0, inputString);
              }
              return false;
            }
        ).collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> {
          // Not using getAllMatches due to test coverage.
          Pattern patternObj = Pattern.compile(entry.getValue().toString(), 0);
          Matcher matcher = patternObj.matcher(inputString);

          List<String> matches = new ArrayList<>();
          while (matcher.find()) {
            matches.add(matcher.group());
          }
          return matches;
        }));
  }

  private static String generateString(int length, String characterSet) {
    StringBuilder buffer = new StringBuilder();
    IntStream.range(0, length)
        .forEach(i -> buffer.append(
            characterSet
                .charAt(randomObj.nextInt(characterSet.length()))
            )
        );
    logger.debug(
        I18nUtility.getFormattedString(
            "RegexUtility.generatedStringFromCharacterSet",
            buffer.toString(),
            length,
            characterSet
        )
    );
    return buffer.toString();
  }

  /**
   * Checks if a regex expression is valid.
   *
   * @param regex The regex expression to evaluate.
   * @return True if the regex expression is valid, false otherwise.
   */
  public static boolean isValidRegex(String regex) {
    boolean result;
    try {
      Pattern.compile(regex);
      result = true;
    } catch (PatternSyntaxException e) {
      result = false;
      logger.debug(
          I18nUtility.getFormattedString("RegexUtility.exception.invalidRegex", regex)
      );
    }
    return result;
  }

  /**
   * Generate a partial regex string that matches the provided regex. Currently it does not support
   * the following: - Ranges (e.g. a-z, 1-9) - Special characters (e.g. $, &) except "." - \S, \W,
   * \D - Character classes (e.g. [...]) - Lazy quantifiers - Anchors and boundaries
   *
   * @param regex             Regex to match.
   * @param randomValueLength Maximum length of value to insert in the generated string.
   * @return The generated string.
   */
  public static String fillRandomValues(String regex, int randomValueLength)
      throws InvalidRegexException {

    if (!isValidRegex(regex)) {
      throw new InvalidRegexException(regex);
    }

    String result = regex;
    List<String> regexCharacterSets;

    regexCharacterSets = getAllMatches(
        PropertyUtility.getProperty(
            "RegexUtility.regex.string.zeroOrMorePairBrackets"
        ),
        0,
        result)
        .parallelStream()
        .map(matchList -> matchList.get(1))
        .distinct()
        .collect(Collectors.toList());

    result = regexCharacterSets.parallelStream()
        .reduce(
            result,
            (subResult, regexCharacterSet) -> subResult.replaceAll(
                String.format(
                    PropertyUtility.getProperty(
                        "RegexUtility.regex.stringFormat.zeroOrMorePairBrackets"),
                    regexCharacterSet),
                generateString(randomObj.nextInt(randomValueLength + 1), regexCharacterSet)
            ));

    //[]+
    regexCharacterSets = getAllMatches(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneOrMorePairBrackets"),
        0,
        result)
        .parallelStream()
        .map(matchList -> matchList.get(1))
        .distinct()
        .collect(Collectors.toList());

    result = regexCharacterSets.parallelStream()
        .reduce(
            result,
            (subResult, regexCharacterSet) -> subResult.replaceAll(
                String.format(
                    PropertyUtility.getProperty(
                        "RegexUtility.regex.stringFormat.oneOrMorePairBrackets"),
                    regexCharacterSet),
                generateString(randomObj.nextInt(randomValueLength + 1), regexCharacterSet)
            ));

    regexCharacterSets = getAllMatches(
        PropertyUtility.getProperty("RegexUtility.regex.string.onePairBrackets"),
        0,
        result)
        .parallelStream()
        .map(matchList -> matchList.get(1))
        .distinct()
        .collect(Collectors.toList());

    result = regexCharacterSets.parallelStream()
        .reduce(result, (subResult, regexCharacterSet) -> subResult.replaceAll(
            String.format(
                PropertyUtility.getProperty(
                    "RegexUtility.regex.stringFormat.onePairBrackets"),
                regexCharacterSet),
            generateString(1, regexCharacterSet)
        ));

    // Scenario when there special character . is provided in the inputPattern.
    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.zeroOrMoreDotCharacter"),
        "$1" + generateString(
            randomObj.nextInt(randomValueLength + 1),
            regexWordCharacters
        )
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneOrMoreDotCharacter"),
        "$1" + generateString(randomValueLength, regexWordCharacters
        )
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneDotCharacter"),
        "$1" + generateString(1, regexWordCharacters
        )
    );

    // Case for \w in inputPattern.
    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.zeroOrMoreWordCharacter"),
        generateString(randomObj.nextInt(randomValueLength + 1), regexWordCharacters)
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneOrMoreWordCharacter"),
        generateString(randomValueLength, regexWordCharacters)
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneWordCharacter"),
        generateString(1, regexWordCharacters)
    );

    // Case for \d in inputPattern.

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.zeroOrMoreDigitCharacter"),
        generateString(randomObj.nextInt(randomValueLength + 1), regexDigitCharacters)
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneOrMoreDigitCharacter"),
        generateString(randomValueLength, regexDigitCharacters)
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneDigitCharacter"),
        generateString(1, regexDigitCharacters)
    );

    // Case for \s in inputPattern.
    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.zeroOrMoreWhiteSpaceCharacter"),
        String.join("", Collections.nCopies(randomObj.nextInt(randomValueLength + 1), " ")));

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneOrMoreWhiteSpaceCharacter"),
        String.join("", Collections.nCopies(randomValueLength, " "))
    );

    result = result.replaceAll(
        PropertyUtility.getProperty("RegexUtility.regex.string.oneWhiteSpaceCharacter"),
        " "
    );

    return result;
  }

  /**
   * Generate regex expression for the following numbers.
   *
   * @param number   The number to generate a regex based on.
   * @param sqlRegex If true, we generate a regex that can be used with SQL queries, else it
   *                 generates a normal regex
   * @return The generated regexps, out of which even if one of them match a value, it is lesser
   *     than the specified number.
   */
  public static String[] generateRegexExpressionsForPositiveNumbersLesserThan(int number,
      Comparison comparison, boolean sqlRegex) {

    if (number < 0 || comparison == Comparison.GREATER
        || comparison == Comparison.GREATER_OR_EQUAL || comparison == Comparison.UNEQUAL) {
      throw new IllegalArgumentException();
    }

    String valueString = Integer.toString(number);
    int[] digits = valueString.chars().map(c -> c - '0').toArray();
    List<String> regexExpressions = new ArrayList<>();

    String allPossibleDigitValues = sqlRegex
        ?
        PropertyUtility.getProperty("RegexUtility.regex.digitsCompleteRange") :
        PropertyUtility.getProperty("RegexUtility.regex.digitCharacter");

    if (comparison == Comparison.EQUAL || comparison == Comparison.LESSER_OR_EQUAL) {
      regexExpressions.add(Integer.toString(number));
    }

    // Generates regexExpressions to match all numbers with the same number of digits,
    // but lesser in value
    // Generates regex to numbers that are lesser in number of digits
    if (comparison == Comparison.LESSER || comparison == Comparison.LESSER_OR_EQUAL) {
      IntStream.range(1, digits.length - 1)
          .filter(digitIndex -> digits[digitIndex] > 0)
          .mapToObj(digitIndex -> valueString.substring(0, digitIndex)
                  + String.format(
              PropertyUtility.getProperty("RegexUtility.regex.digitsWithEndRange"),
              Math.max(digits[digitIndex] - 1, 0)
              )
                  + allPossibleDigitValues.repeat(digits.length - (digitIndex + 1))
          )
          .forEach(regexExpressions::add);

      if (digits[digits.length - 1] > 0) {
        regexExpressions.add(valueString.substring(0, valueString.length() - 1)
            + String.format(
            PropertyUtility.getProperty("RegexUtility.regex.digitsWithEndRange"),
            Math.max(digits[digits.length - 1] - 1, 0)
        ));
      }
      IntStream.range(1, digits.length)
          .mapToObj(index -> String.format(
              PropertyUtility.getProperty("RegexUtility.regex.stringFormat.stringBeginningAndEnd"),
              allPossibleDigitValues.repeat(index)
          ))
          .forEach(regexExpressions::add);
    }

    return regexExpressions.toArray(new String[0]);
  }

}

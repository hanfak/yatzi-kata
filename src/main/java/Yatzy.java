import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByKey;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

// I am keeping the api of the methods the same, would use an interface but we have  static methods, avoids breaking backwards compatibility of existing users
public class Yatzy {
  // TODO Extra test to validate value passed in is between 1 and 6, to apply to all methods and constructor,
  //  if had object used for param could hide this. use annotation?library?

  private final int[] diceScores;

  public Yatzy(int dice1, int dice2, int dice3, int dice4, int dice5) {
    this.diceScores = new int[]{dice1, dice2, dice3, dice4, dice5};
  }

  public static int chance(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return dice1 + dice2 + dice3 + dice4 + dice5;
  }

  public static int yatzy(int dice1, int dice2, int dice3, int dice4, int dice5) {
    Set<Integer> uniqueDice = Stream.of(dice1, dice2, dice3, dice4, dice5)
            .collect(toCollection(HashSet::new));
    return uniqueDice.size() == 1L ? 50 :0;
  }

  public static int ones(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    return sumOfDieWithScore(1, diceScores);
  }

  public static int twos(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    return sumOfDieWithScore(2, diceScores);
  }

  public static int threes(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    return sumOfDieWithScore(3, diceScores);
  }

  public int fours() {
    List<Integer> diceScores = Arrays.stream(this.diceScores).boxed().collect(toList());
    return sumOfDieWithScore(4, diceScores);
  }

  public int fives() {
    List<Integer> diceScores = Arrays.stream(this.diceScores).boxed().collect(toList());
    return sumOfDieWithScore(5, diceScores);
  }

  public int sixes() {
    List<Integer> diceScores = Arrays.stream(this.diceScores).boxed().collect(toList());
    return sumOfDieWithScore(6, diceScores);
  }

  // TODO different way
  public static int score_pair(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    Predicate<Map.Entry<Integer, Long>> twoDiceWithSameScore = hasAtLeastNDiceScoresTheSame(2L);
    Comparator<Map.Entry<Integer, Long>> dieScorePair = comparingByKey();

    return calculateSinglePairYatzyScore(diceScores, twoDiceWithSameScore, dieScorePair, 2);
  }

  public static int four_of_a_kind(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    Predicate<Map.Entry<Integer, Long>> atLeastFourDiceWithSameScore = hasAtLeastNDiceScoresTheSame(4L);
    Comparator<Map.Entry<Integer, Long>> dieScorePair = (a1, a2) -> 0;

    return calculateSinglePairYatzyScore(diceScores, atLeastFourDiceWithSameScore, dieScorePair, 4);
  }

  public static int three_of_a_kind(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    Predicate<Map.Entry<Integer, Long>> atLeastThreeDiceWithSameScore = hasAtLeastNDiceScoresTheSame(3L);
    Comparator<Map.Entry<Integer, Long>> dieScorePair = (a1, a2) -> 0; // equivalent to empty comparator

    return calculateSinglePairYatzyScore(diceScores, atLeastThreeDiceWithSameScore, dieScorePair, 3);
  }

  public static int two_pair(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);

    List<Map.Entry<Integer, Long>> applicableDiceScore = diceScorePerCount(diceScores).stream()
            .filter(hasAtLeastNDiceScoresTheSame(2L))
            .collect(toList());

    if (applicableDiceScore.size() < 2) { // Can one line it but good to be consistent with formatting
      return 0;
    }
    return applicableDiceScore.stream()
            .map(dieScoreByCount -> dieScoreByCount.getKey() * 2)
            .reduce(0, Integer::sum);
  }

  public static int smallStraight(int d1, int d2, int d3, int d4, int d5) {
    int[] tallies;
    tallies = new int[6];
    tallies[d1 - 1] += 1;
    tallies[d2 - 1] += 1;
    tallies[d3 - 1] += 1;
    tallies[d4 - 1] += 1;
    tallies[d5 - 1] += 1;
    if (tallies[0] == 1 &&
            tallies[1] == 1 &&
            tallies[2] == 1 &&
            tallies[3] == 1 &&
            tallies[4] == 1)
      return 15;
    return 0;
  }

  public static int largeStraight(int d1, int d2, int d3, int d4, int d5) {
    int[] tallies;
    tallies = new int[6];
    tallies[d1 - 1] += 1;
    tallies[d2 - 1] += 1;
    tallies[d3 - 1] += 1;
    tallies[d4 - 1] += 1;
    tallies[d5 - 1] += 1;
    if (tallies[1] == 1 &&
            tallies[2] == 1 &&
            tallies[3] == 1 &&
            tallies[4] == 1
            && tallies[5] == 1)
      return 20;
    return 0;
  }

  public static int fullHouse(int d1, int d2, int d3, int d4, int d5) {
    int[] tallies;
    boolean _2 = false;
    int i;
    int _2_at = 0;
    boolean _3 = false;
    int _3_at = 0;


    tallies = new int[6];
    tallies[d1 - 1] += 1;
    tallies[d2 - 1] += 1;
    tallies[d3 - 1] += 1;
    tallies[d4 - 1] += 1;
    tallies[d5 - 1] += 1;

    for (i = 0; i != 6; i += 1)
      if (tallies[i] == 2) {
        _2 = true;
        _2_at = i + 1;
      }

    for (i = 0; i != 6; i += 1)
      if (tallies[i] == 3) {
        _3 = true;
        _3_at = i + 1;
      }

    if (_2 && _3)
      return _2_at * 2 + _3_at * 3;
    else
      return 0;
  }

  private static Integer sumOfDieWithScore(Integer score, List<Integer> diceScores) {
    return diceScores.stream()
            .filter(score::equals)
            .reduce(0, Integer::sum);
  }

  private static Predicate<Map.Entry<Integer, Long>> hasAtLeastNDiceScoresTheSame(long numberOfDices) {
    return diceScoreCount -> diceScoreCount.getValue().compareTo(numberOfDices) >= 0;
  }

  private static int calculateSinglePairYatzyScore(List<Integer> diceScores, Predicate<Map.Entry<Integer, Long>> twoDiceWithSameScore, Comparator<Map.Entry<Integer, Long>> dieScoreToUse, int multiplier) {
    return diceScorePerCount(diceScores).stream()
            .filter(twoDiceWithSameScore)
            .max(dieScoreToUse)
            .map(dieScoreByCount -> dieScoreByCount.getKey() * multiplier) // Could use method ref, by doing mult after stream finished
            .orElse(0);
  }

  private static Set<Map.Entry<Integer, Long>> diceScorePerCount(List<Integer> diceScores) {
    return diceScores.stream()
            .collect(groupingBy(identity(), counting())).entrySet();
  }
}
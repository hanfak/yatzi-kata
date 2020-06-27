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

  private final int[] diceScorces;

  public Yatzy(int dice1, int dice2, int dice3, int dice4, int dice5) {
    this.diceScorces = new int[]{dice1, dice2, dice3, dice4, dice5};
  }

  public static int chance(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return dice1 + dice2 + dice3 + dice4 + dice5;
  }

  public static int yatzy(int dice1, int dice2, int dice3, int dice4, int dice5) {
    Set<Integer> uniqueDice = Stream.of(dice1, dice2, dice3, dice4, dice5)
            .collect(toCollection(HashSet::new));
    if (uniqueDice.size() == 1L) {
      return 50;
    }
    return 0;
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
    List<Integer> diceScores = Arrays.stream(this.diceScorces).boxed().collect(toList());
    return sumOfDieWithScore(4, diceScores);
  }

  public int fives() {
    List<Integer> diceScores = Arrays.stream(this.diceScorces).boxed().collect(toList());
    return sumOfDieWithScore(5, diceScores);
  }

  public int sixes() {
    List<Integer> diceScores = Arrays.stream(this.diceScorces).boxed().collect(toList());
    return sumOfDieWithScore(6, diceScores);
  }

  public static int score_pair(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    Map<Integer, Long> countOfEachDieScore = diceScores.stream()
            .collect(groupingBy(identity(), counting()));
    Predicate<Map.Entry<Integer, Long>> twoDiceWithSameScore = diceScoreCount -> diceScoreCount.getValue().equals(2L);
    return countOfEachDieScore.entrySet().stream()
            .filter(twoDiceWithSameScore)
            .max(comparingByKey())
            .map(Yatzy::calculateTotalScore)
            .orElse(0); // Not tested, no rules for this, this is assumption, but in prior code it returns 0
  }

  public static int two_pair(int d1, int d2, int d3, int d4, int d5) {
    int[] counts = new int[6];
    counts[d1 - 1]++;
    counts[d2 - 1]++;
    counts[d3 - 1]++;
    counts[d4 - 1]++;
    counts[d5 - 1]++;
    int n = 0;
    int score = 0;
    for (int i = 0; i < 6; i += 1)
      if (counts[6 - i - 1] >= 2) {
        n++;
        score += (6 - i);
      }
    if (n == 2)
      return score * 2;
    else
      return 0;
  }

  public static int four_of_a_kind(int _1, int _2, int d3, int d4, int d5) {
    int[] tallies;
    tallies = new int[6];
    tallies[_1 - 1]++;
    tallies[_2 - 1]++;
    tallies[d3 - 1]++;
    tallies[d4 - 1]++;
    tallies[d5 - 1]++;
    for (int i = 0; i < 6; i++)
      if (tallies[i] >= 4)
        return (i + 1) * 4;
    return 0;
  }

  public static int three_of_a_kind(int d1, int d2, int d3, int d4, int d5) {
    int[] t;
    t = new int[6];
    t[d1 - 1]++;
    t[d2 - 1]++;
    t[d3 - 1]++;
    t[d4 - 1]++;
    t[d5 - 1]++;
    for (int i = 0; i < 6; i++)
      if (t[i] >= 3)
        return (i + 1) * 3;
    return 0;
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

  private static int calculateTotalScore(Map.Entry<Integer, Long> dieScoreByCount) {
    return dieScoreByCount.getKey() * 2;
  }
}
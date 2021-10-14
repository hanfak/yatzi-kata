import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

import static java.util.Map.Entry.comparingByKey;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

// I am keeping the api of the methods the same, would use an interface but we have  static methods, avoids breaking backwards compatibility of existing users
// TODO put back original method names
public class Yatzy {
  // TODO extract out magic numbers

  private final List<Integer> diceScores; // TODO should be set as emptyList
  
 // TODO: protected int[] dice; should be brought back, as could still be used due to protected, and just reference a copy of diceScores

  // Should either use all static or all instance, but impl either one will break backward compatibility
  public Yatzy(int dice1, int dice2, int dice3, int dice4, int dice5) {
    this.diceScores = createDiceScores(dice1, dice2, dice3, dice4, dice5); // Not great better do logic/validation in static factory method
  }

  public static int chance(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return dice1 + dice2 + dice3 + dice4 + dice5;
  }

  public static int yatzy(int dice1, int dice2, int dice3, int dice4, int dice5) {
    Set<Integer> uniqueDice = new HashSet<>(createDiceScores(dice1, dice2, dice3, dice4, dice5));
    return uniqueDice.size() == 1L ? 50 : 0;
  }

  public static int ones(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return sumOfDieWithScore(1, createDiceScores(dice1, dice2, dice3, dice4, dice5));
  }

  public static int twos(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return sumOfDieWithScore(2, createDiceScores(dice1, dice2, dice3, dice4, dice5));
  }

  public static int threes(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return sumOfDieWithScore(3, createDiceScores(dice1, dice2, dice3, dice4, dice5));
  }

  public int fours() {
    return sumOfDieWithScore(4, this.diceScores);
  }

  public int fives() {
    return sumOfDieWithScore(5, this.diceScores);
  }

  public int sixes() {
    return sumOfDieWithScore(6, this.diceScores);
  }

  // TODO different way
  // TODO extrat lots of repetition betwee the next three methods
  public static int scorePair(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = createDiceScores(dice1, dice2, dice3, dice4, dice5);
    Predicate<Entry<Integer, Long>> twoDiceWithSameScore = hasAtLeastNDiceScoresTheSame(2L);
    Comparator<Entry<Integer, Long>> dieScorePair = comparingByKey();

    return calculateSinglePairYatzyScore(diceScores, twoDiceWithSameScore, dieScorePair, 2);
  }

  public static int fourOfAKind(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = createDiceScores(dice1, dice2, dice3, dice4, dice5);
    Predicate<Entry<Integer, Long>> atLeastFourDiceWithSameScore = hasAtLeastNDiceScoresTheSame(4L);
    Comparator<Entry<Integer, Long>> dieScorePair = (a1, a2) -> 0;

    return calculateSinglePairYatzyScore(diceScores, atLeastFourDiceWithSameScore, dieScorePair, 4);
  }

  public static int threeOfAKind(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = createDiceScores(dice1, dice2, dice3, dice4, dice5);
    Predicate<Entry<Integer, Long>> atLeastThreeDiceWithSameScore = hasAtLeastNDiceScoresTheSame(3L);
    Comparator<Entry<Integer, Long>> dieScorePair = (a1, a2) -> 0; // equivalent to empty comparator

    return calculateSinglePairYatzyScore(diceScores, atLeastThreeDiceWithSameScore, dieScorePair, 3);
  }

  // TODO: better way?? similar to full house
  public static int twoPair(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = createDiceScores(dice1, dice2, dice3, dice4, dice5);

    List<Entry<Integer, Long>> applicableDiceScore = diceScorePerCount(diceScores).stream()
            .filter(hasAtLeastNDiceScoresTheSame(2L))
            .collect(toList());

    if (applicableDiceScore.size() < 2) { // Can one line it but good to be consistent with formatting
      return 0;
    }
    return applicableDiceScore.stream()
            .map(dieScoreByCount -> dieScoreByCount.getKey() * 2)
            .reduce(0, Integer::sum);
  }

  public static int smallStraight(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return calculateScoreForStraight(createDiceScores(dice1, dice2, dice3, dice4, dice5), 1);
  }

  public static int largeStraight(int dice1, int dice2, int dice3, int dice4, int dice5) {
    return calculateScoreForStraight(createDiceScores(dice1, dice2, dice3, dice4, dice5), 6);
  }

  // TODO simpilfy
  public static int fullHouse(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = createDiceScores(dice1, dice2, dice3, dice4, dice5);
    Predicate<Entry<Integer, Long>> twoDiceWithSameScore = diceScoreCount -> diceScoreCount.getValue().compareTo(2L) == 0;
    Predicate<Entry<Integer, Long>> atLeastThreeDiceWithSameScore = diceScoreCount -> diceScoreCount.getValue().compareTo(3L) == 0;
    Comparator<Entry<Integer, Long>> dieScorePair = comparingByKey();

    int scoreForAPair = calculateSinglePairYatzyScore(diceScores, twoDiceWithSameScore, dieScorePair, 2);
    int scoreForAThreeOfAKind = calculateSinglePairYatzyScore(diceScores, atLeastThreeDiceWithSameScore, (a1, a2) -> 0, 3);

    if (scoreForAPair == 0 || scoreForAThreeOfAKind == 0) {
      return 0;
    }
    return scoreForAPair + scoreForAThreeOfAKind;
  }

  private static List<Integer> createDiceScores(int dice1, int dice2, int dice3, int dice4, int dice5) {
    List<Integer> diceScores = Arrays.asList(dice1, dice2, dice3, dice4, dice5);
    long count = diceScores.stream()
            .filter(score -> score > 0 && score < 7)
            .count();
    if (count != 5) {
      throw new IllegalArgumentException("Dice score must be between 1 and 6 inclusive");
    }
    return diceScores;
  }

  private static Integer sumOfDieWithScore(Integer score, List<Integer> diceScores) {
    return diceScores.stream()
            .filter(score::equals)
            .reduce(0, Integer::sum);
  }

  private static Predicate<Entry<Integer, Long>> hasAtLeastNDiceScoresTheSame(long numberOfDices) {
    return diceScoreCount -> diceScoreCount.getValue().compareTo(numberOfDices) >= 0;
  }

  private static int calculateSinglePairYatzyScore(List<Integer> diceScores, Predicate<Entry<Integer, Long>> twoDiceWithSameScore, Comparator<Entry<Integer, Long>> dieScoreToUse, int multiplier) {
    return diceScorePerCount(diceScores).stream()
            .filter(twoDiceWithSameScore)
            .max(dieScoreToUse)
            .map(dieScoreByCount -> dieScoreByCount.getKey() * multiplier) // Could use method ref, by doing mult after stream finished
            .orElse(0);
  }

  private static Set<Entry<Integer, Long>> diceScorePerCount(List<Integer> diceScores) {
    return diceScores.stream()
            .collect(groupingBy(identity(), counting())).entrySet();
  }

  private static int calculateScoreForStraight(List<Integer> diceScores, int dieScoreToCount) {
    boolean notAStraight = diceScores.stream().distinct().count() == 5L;
    if (notAStraight && diceScores.contains(dieScoreToCount)) {
      return diceScores.stream().sorted().reduce(0, Integer::sum);
    }
    return 0;
  }
}

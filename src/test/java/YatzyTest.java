import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class YatzyTest {

  @Test
  public void chanceScoresSumOfAllDice() {
    assertEquals(15, Yatzy.chance(2, 3, 4, 5, 1));
    assertEquals(16, Yatzy.chance(3, 3, 4, 5, 1));
  }

  @Test
  public void yatzyScores50() {
    assertThatThrownBy(() -> Yatzy.yatzy(-1, 12, 0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(50, Yatzy.yatzy(4, 4, 4, 4, 4));
    assertEquals(50, Yatzy.yatzy(6, 6, 6, 6, 6));
    assertEquals(0, Yatzy.yatzy(6, 6, 6, 6, 3));
  }

  @Test
  public void multiple1sShouldScoreSomeOf1s() {
    assertThatThrownBy(() -> Yatzy.ones(15, 2, 3, 4, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");

    assertEquals(1, Yatzy.ones(1, 2, 3, 4, 5));
    assertEquals(2, Yatzy.ones(1, 2, 1, 4, 5));
    assertEquals(0, Yatzy.ones(6, 2, 2, 4, 5));
    assertEquals(4, Yatzy.ones(1, 2, 1, 1, 1));
  }

  @Test
  public void multiple2sShouldScoreSomeOf2s() {
    assertThatThrownBy(() -> Yatzy.twos(1, 21, 3, 4, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(4, Yatzy.twos(1, 2, 3, 2, 6));
    assertEquals(10, Yatzy.twos(2, 2, 2, 2, 2));
  }

  @Test
  public void multiple3sShouldScoreSomeOf3s() {
    assertThatThrownBy(() -> Yatzy.threes(1, 2, 33, 4, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(6, Yatzy.threes(1, 2, 3, 2, 3));
    assertEquals(12, Yatzy.threes(2, 3, 3, 3, 3));
  }

  @Test
  public void multiple4sShouldScoreSomeOf4s() {
    assertThatThrownBy(() -> new Yatzy(4, 4, 4, 51, 5).fours())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(12, new Yatzy(4, 4, 4, 5, 5).fours());
    assertEquals(8, new Yatzy(4, 4, 5, 5, 5).fours());
    assertEquals(4, new Yatzy(4, 5, 5, 5, 5).fours());
  }

  @Test
  public void multiple5sShouldScoreSomeOf5s() {
    assertThatThrownBy(() -> new Yatzy(4, 4, 4, 5, 54).fives())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(10, new Yatzy(4, 4, 4, 5, 5).fives());
    assertEquals(15, new Yatzy(4, 4, 5, 5, 5).fives());
    assertEquals(20, new Yatzy(4, 5, 5, 5, 5).fives());
  }

  @Test
  public void multiple6sShouldScoreSomeOf6s() {
    assertThatThrownBy(() -> new Yatzy(4, 41, 4, 5, 54).sixes())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(0, new Yatzy(4, 4, 4, 5, 5).sixes());
    assertEquals(6, new Yatzy(4, 4, 6, 5, 5).sixes());
    assertEquals(18, new Yatzy(6, 5, 6, 6, 5).sixes());
  }

  @Test
  public void mulitpleSinglePairsShouldScoreSumOfHighestPair() {
    assertThatThrownBy(() -> Yatzy.score_pair(4, 41, 4, 5, 54))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(6, Yatzy.score_pair(3, 4, 3, 5, 6));
    assertEquals(10, Yatzy.score_pair(5, 3, 3, 3, 5));
    assertEquals(12, Yatzy.score_pair(5, 3, 6, 6, 5));
    assertEquals(0, Yatzy.score_pair(5, 3, 2, 6, 1));
  }

  @Test
  public void aThreeOfAKindShouldScoreSumOfThreeOfAKind() {
    assertThatThrownBy(() -> Yatzy.three_of_a_kind(0, 4, 4, 5, 4))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(9, Yatzy.three_of_a_kind(3, 3, 3, 4, 5));
    assertEquals(15, Yatzy.three_of_a_kind(5, 3, 5, 4, 5));
    assertEquals(9, Yatzy.three_of_a_kind(3, 3, 3, 3, 5));
  }

  @Test
  public void aFourOfAKindShouldScoreSumOfFourOfAKind() {
    assertThatThrownBy(() -> Yatzy.four_of_a_kind(0, 4, 4, 0, 4))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(12, Yatzy.four_of_a_kind(3, 3, 3, 3, 5));
    assertEquals(20, Yatzy.four_of_a_kind(5, 5, 5, 4, 5));
    assertEquals(9, Yatzy.three_of_a_kind(3, 3, 3, 3, 3));
  }

  @Test
  public void aTwoPairShouldScoreSumOfTwoPair() {
    assertThatThrownBy(() -> Yatzy.two_pair(0, 0, 0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(16, Yatzy.two_pair(3, 3, 5, 4, 5));
    assertEquals(16, Yatzy.two_pair(3, 3, 5, 5, 5));
    assertEquals(0, Yatzy.two_pair(3, 1, 5, 2, 5));
    assertEquals(0, Yatzy.two_pair(3, 1, 5, 5, 5));
    assertEquals(0, Yatzy.two_pair(3, 1, 4, 2, 5));
//    assertEquals(20, Yatzy.two_pair(5, 1, 5, 5, 5)); // Is this rule applicable?? depends
  }

  @Test
  public void aSmallStraightShouldScoreSumOfAllCards() {
    assertThatThrownBy(() -> Yatzy.smallStraight(10, 10, 10, 10, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(15, Yatzy.smallStraight(1, 2, 3, 4, 5));
    assertEquals(15, Yatzy.smallStraight(2, 3, 4, 5, 1));
    assertEquals(0, Yatzy.smallStraight(1, 2, 2, 4, 5));
    assertEquals(0, Yatzy.smallStraight(6, 2, 3, 4, 5));
  }

  @Test
  public void aLargeStraightShouldScoreSumOfAllCards() {
    assertThatThrownBy(() -> Yatzy.largeStraight(0, 10, 0, 50, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(20, Yatzy.largeStraight(6, 2, 3, 4, 5));
    assertEquals(20, Yatzy.largeStraight(2, 3, 4, 5, 6));
    assertEquals(0, Yatzy.largeStraight(1, 2, 2, 4, 5));
    assertEquals(0, Yatzy.largeStraight(1, 2, 3, 4, 5));

  }

  @Test
  public void aFullHouseShouldScoreTheSumOfAllCards() {
   assertThatThrownBy(() -> Yatzy.fullHouse(0, 0, 0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dice score must be between 1 and 6 inclusive");
    assertEquals(18, Yatzy.fullHouse(6, 2, 2, 2, 6));
    assertEquals(22, Yatzy.fullHouse(6, 6, 2, 2, 6));
    assertEquals(0, Yatzy.fullHouse(2, 3, 4, 5, 6));
    assertEquals(0, Yatzy.fullHouse(6, 6, 1, 2, 6));
    assertEquals(0, Yatzy.fullHouse(1, 2, 3, 4, 5));
    assertEquals(0, Yatzy.fullHouse(3, 3, 5, 4, 5));
    assertEquals(0, Yatzy.fullHouse(3, 3, 3, 3, 3));
  }
}

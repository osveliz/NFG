package games;

import java.util.*;

/**
 * An Iterator for looping through the outcomes in the game
 */

public final class OutcomeIterator implements Iterator<int[]> {
  private final int[] nActions;
  private final int nPlayers;

  private int[] actions;
  private boolean firstOutcome;

  public OutcomeIterator(Game g) {
    this.nPlayers = g.getNumPlayers();
    this.nActions = g.getNumActions();
    init();
  }

  public OutcomeIterator(int numPlayers, int[] numActions) {
    this.nPlayers = numPlayers;
    this.nActions = numActions.clone();
    init();
  }

  public OutcomeIterator(GameObserver go) {
    this.nPlayers = go.getNumPlayers();
    this.nActions = go.getNumActions();
    init();
  }

  private void init() {
    actions = new int[nPlayers];
    Arrays.fill(actions, 1);
    firstOutcome = true;
  }

  public Iterator iterator() {
    return this;
  }

  public void reset() {
    firstOutcome = true;
    Arrays.fill(actions, 1);
  }

  public int[] getOutcome() {
    return actions;
  }

  public boolean hasNext() {
    if (firstOutcome) return true;
    for (int i = 0; i < nPlayers; i++) {
      if (actions[i] < nActions[i]) {
        return true;
      }
    }
    return false;
  }

  /**
   * In a two by two matrix game, the outcomes are looped over
   * in the order top left, bottom left, top right, bottom right.
   * Can extend this idea of first player's actions being looped
   * through quickly and repeatedly, and last player's actions
   * being looped through slowly and only once to figure out
   * ordering for games of other sizes.
   */
  public int[] next() {
    // nothing left
    if (!hasNext()) return actions;

    if (firstOutcome) {
      firstOutcome = false;
      return actions;
    }

    for (int i = 0; i < nPlayers; i++) {
      if (actions[i] < nActions[i]) {
        actions[i]++;
        break;
      } else {
        actions[i] = 1;
      }
    }
    return actions;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public String toString() {
    StringBuilder sb = EGAUtils.getSB();
    sb.append("[");
    for (int i = 0; i < nPlayers; i++) {
      sb.append(actions[i]).append((i < nPlayers - 1 ? "  " : "]"));
    }
    return EGAUtils.returnSB(sb);
  }
}


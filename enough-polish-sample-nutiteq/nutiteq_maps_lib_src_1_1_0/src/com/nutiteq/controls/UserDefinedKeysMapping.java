package com.nutiteq.controls;

import com.nutiteq.utils.Utils;

/**
 * Default control keys mapping used inside library. Developer needs to define
 * keys for actions defined in {@link com.nutiteq.controls.ControlKeys}.
 * Multiple keys can be used for same action.
 */
public class UserDefinedKeysMapping implements ControlKeysHandler {
  private int[] controlKeys;
  private int[] controlActions;

  public UserDefinedKeysMapping() {
    controlKeys = new int[0];
    controlActions = new int[0];
  }

  /**
   * Define controlaction for key code.
   * 
   * @param actionCode
   *          action code
   * @param keyCode
   *          key code
   */
  public void defineKey(final int actionCode, final int keyCode) {
    if (actionCode <= ControlKeys.NO_ACTION_KEY || actionCode > ControlKeys.SELECT_KEY) {
      throw new IllegalArgumentException("Invalid action code!");
    }

    final int keyPos = Utils.binarySearch(controlKeys, keyCode);
    if (keyPos < 0) {
      controlKeys = appendInt(controlKeys, keyCode);
      controlActions = appendInt(controlActions, actionCode);
      Utils.doubleBubbleSort(controlKeys, controlActions);
    } else {
      controlActions[keyPos] = actionCode;
    }
  }

  private int[] appendInt(final int[] array, final int append) {
    if (array.length == 0) {
      return new int[] { append };
    }

    final int[] result = new int[array.length + 1];
    System.arraycopy(array, 0, result, 0, array.length);
    result[result.length - 1] = append;
    return result;
  }

  /*
   * @see com.nutiteq.controls.ControlKeysHandler#getKeyActionCode(int)
   */
  public int getKeyActionCode(final int keyCode) {
    if (controlKeys.length == 0) {
      return ControlKeys.NO_ACTION_KEY;
    }

    final int actionKeyPosition = Utils.binarySearch(controlKeys, keyCode);

    if (actionKeyPosition < 0) {
      return ControlKeys.NO_ACTION_KEY;
    }

    return controlActions[actionKeyPosition];
  }
}

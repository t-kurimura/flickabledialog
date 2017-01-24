package com.tkurimura.flickabledialog;

public interface FlickableDialogListener {
  public enum X_DIRECTION {

    LEFT_TOP,
    RIGHT_TOP,
    RIGHT_BOTTOM,
    LEFT_BOTTOM
  }

  /**
   * Basic helper implementation of FlickableDialogListener with empty handlers to facilitate
   * implementations that don't care about some actions
   */
  public static class FlickableDialogListenerBasic implements FlickableDialogListener {

    @Override public void onFlickableDialogFlicked(X_DIRECTION xDirection) { }
    @Override public void onFlickableDialogCanceled() { }
  }

  /**
   * callback flicking direction in categorization of X area
   *
   * @param xDirection LEFT_TOP,RIGHT_TOP,RIGHT_BOTTOM,LEFT_BOTTOM
   * @version 0.3.0
   */
  void onFlickableDialogFlicked(X_DIRECTION xDirection);

  /**
   * callback touched outside or pressed back key.
   *
   * @version 0.4.0
   */
  void onFlickableDialogCanceled();
}

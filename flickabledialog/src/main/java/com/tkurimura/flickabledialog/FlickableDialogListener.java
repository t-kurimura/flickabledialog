package com.tkurimura.flickabledialog;

import android.support.annotation.Nullable;

public class FlickableDialogListener {

  public FlickableDialogListener(FlickableDialog flickableDialog) {

    Object anyListener = flickableDialog.getParentFragment();
    if (anyListener == null) {
      anyListener = flickableDialog.getParentFragment();
      if (anyListener == null) {
        throw new IllegalStateException(
            "You may implement getSupportFragmentManager() into  FlickableDialog#show argument in Fragment . If you call FlickableDialog in Fragment, you have to use getChildFragmentManager instead of getSupportFragmentManager");
      }
    }
    if (anyListener instanceof OnFlickedXDirection) {
      onFlickedXDirectionListener = (OnFlickedXDirection) anyListener;
    }
    if (anyListener instanceof OnCanceled) {
      onFlickableDialogCanceled = (OnCanceled) anyListener;
    }
  }

  public enum X_DIRECTION {

    LEFT_TOP,
    RIGHT_TOP,
    RIGHT_BOTTOM,
    LEFT_BOTTOM
  }

  public interface OnFlickedXDirection {
    /**
     * callback flicking direction in categorization of X area
     *
     * @param xDirection LEFT_TOP,RIGHT_TOP,RIGHT_BOTTOM,LEFT_BOTTOM
     * @version 0.3.0
     */
    void onFlickableDialogFlicked(X_DIRECTION xDirection);
  }

  public interface OnCanceled {
    /**
     * callback touched outside or pressed back key.
     *
     * @version 0.4.0
     */
    void onFlickableDialogCanceled();
  }

  @Nullable private OnFlickedXDirection onFlickedXDirectionListener;

  @Nullable private OnCanceled onFlickableDialogCanceled;

  @Nullable OnFlickedXDirection getOnFlickedXDirectionListener() {
    return onFlickedXDirectionListener;
  }

  @Nullable OnCanceled getOnFlickableDialogCanceledListener() {
    return onFlickableDialogCanceled;
  }

  void destroyListeners() {
    onFlickableDialogCanceled = null;
    onFlickedXDirectionListener = null;
  }
}

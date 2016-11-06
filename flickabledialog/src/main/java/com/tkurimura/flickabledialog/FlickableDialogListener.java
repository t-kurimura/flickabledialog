package com.tkurimura.flickabledialog;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class FlickableDialogListener {

  public enum X_DIRECTION {

    LEFT_TOP,
    RIGHT_TOP,
    RIGHT_BOTTOM,
    LEFT_BOTTOM
  }

  public interface OnFlickedCross {
    /**
     * callback flicking direction in categorization of cross area
     *
     * @param xDirection top,right,bottom,left
     * @version 0.3.0
     */
    void onFlicked(X_DIRECTION xDirection);
  }

  public interface OnOriginBack {
    /**
     * callback flicking direction in categorization of X area
     *
     * @version 0.3.0
     */
    void onOriginBack();
  }

  public interface OnFlicking {
    /**
     * callback flicking amount from original position to dismiss threshold.
     *
     * @param verticalPercentage vertical flicking amount(-100 : top, 0 : origin. 100 : right)
     * @param horizontalPercentage horizontal flicking amount(-100 : left, 0 : origin. 100 : right)
     * @version 0.3.0
     */
    void onFlicked(int verticalPercentage, int horizontalPercentage);
  }

  @Nullable private FlickableDialogListener.OnFlickedCross onFlickedCrossListener;

  @Nullable private FlickableDialogListener.OnOriginBack onOriginBackListener;

  @Nullable private FlickableDialogListener.OnFlicking onFlickingListener;

  public void holdListeners(Activity activity) {
    Object anyListener = activity;
    if (anyListener == null) {
      throw new IllegalStateException("cannot attach flickable dialog");
    }

    if (anyListener instanceof FlickableDialogListener.OnFlickedCross) {
      onFlickedCrossListener = (FlickableDialogListener.OnFlickedCross) anyListener;
    }
    if (anyListener instanceof FlickableDialogListener.OnOriginBack) {
      onOriginBackListener = (FlickableDialogListener.OnOriginBack) anyListener;
    }
    if (anyListener instanceof FlickableDialogListener.OnFlicking) {
      onFlickingListener = (FlickableDialogListener.OnFlicking) anyListener;
    }
  }

  @Nullable public OnFlickedCross getOnFlickedCrossListener(FlickableDialog flickableDialog) {

    if (onFlickedCrossListener != null) {
      return onFlickedCrossListener;
    }

    if (flickableDialog.getTargetFragment() instanceof OnFlickedCross) {
      Fragment targetFragment = flickableDialog.getTargetFragment();
      return ((OnFlickedCross) targetFragment);
    }

    return null;
  }

  @Nullable public OnOriginBack getOnOriginBackListener(FlickableDialog flickableDialog) {

    if (onOriginBackListener != null) {
      return onOriginBackListener;
    }

    if (flickableDialog.getTargetFragment() instanceof OnOriginBack) {
      Fragment targetFragment = flickableDialog.getTargetFragment();
      return ((OnOriginBack) targetFragment);
    }

    return null;
  }

  @Nullable public OnFlicking getOnFlickingListener(FlickableDialog flickableDialog) {

    if (onFlickingListener != null) {
      return onFlickingListener;
    }

    if (flickableDialog.getTargetFragment() instanceof OnFlicking) {
      Fragment targetFragment = flickableDialog.getTargetFragment();
      return ((OnFlicking) targetFragment);
    }

    return null;
  }

  public void destroyListeners(){

    onFlickedCrossListener = null;
    onOriginBackListener = null;
    onFlickingListener = null;

  }
}

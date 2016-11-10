package com.tkurimura.flickabledialog;


import android.support.annotation.Nullable;

public class FlickableDialogListener {

  public FlickableDialogListener(FlickableDialog flickableDialog) {

    Object anyListener = flickableDialog.getParentFragment();
    if(anyListener == null){
      anyListener = flickableDialog.getParentFragment();
      if (anyListener == null) {
        throw new IllegalStateException("cannot attach flickable dialog");
      }
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

  @Nullable OnFlickedCross getOnFlickedCrossListener() {
    return onFlickedCrossListener;
  }

  @Nullable OnOriginBack getOnOriginBackListener() {
    return onOriginBackListener;
  }

  @Nullable OnFlicking getOnFlickingListener() {
    return onFlickingListener;
  }

  void destroyListeners(){

    onFlickedCrossListener = null;
    onOriginBackListener = null;
    onFlickingListener = null;

  }
}

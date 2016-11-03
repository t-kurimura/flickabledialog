package com.tkurimura.flickabledialog;


public class FlickableDialogListener {

  enum X_DIRECTION{

    LEFT_TOP,
    RIGHT_TOP,
    RIGHT_BOTTOM,
    LEFT_BOTTOM
  }

  public interface OnFlickedCross{
    /**
     * callback flicking direction in categorization of cross area
     * @version 0.3.0
     * @param xDirection top,right,bottom,left
     */
    void onFlicked(X_DIRECTION xDirection);
  }

  public interface OnOriginBack {
    /**
     * callback flicking direction in categorization of X area
     * @version 0.3.0
     */
    void onOriginBack();
  }

  public interface OnFlicking{
    /**
     * callback flicking amount from original position to dismiss threshold.
     * @version 0.3.0
     * @param verticalPercentage vertical flicking amount(-100 : top, 0 : origin. 100 : right)
     * @param horizontalPercentage horizontal flicking amount(-100 : left, 0 : origin. 100 : right)
     */
    void onFlicked(int verticalPercentage,int horizontalPercentage);
  }
}

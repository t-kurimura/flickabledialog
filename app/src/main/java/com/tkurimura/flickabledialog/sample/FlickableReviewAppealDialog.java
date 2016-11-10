package com.tkurimura.flickabledialog.sample;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;

/**
 * Created by TakahisaKurimura on 2016/11/07.
 */

public class FlickableReviewAppealDialog extends FlickableDialog {

  public static FlickableReviewAppealDialog newInstance(){

    FlickableReviewAppealDialog flackablePremiumAppealDialog = new FlickableReviewAppealDialog();
    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY,R.layout.dialog_review_appeal);
    flackablePremiumAppealDialog.setArguments(bundle);

    return flackablePremiumAppealDialog;
  }

  private View topLeft;
  private View topRight;
  private View bottomLeft;
  private View bottomRight;

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);

    topLeft = dialog.findViewById(R.id.dialog_review_top_left);
    topRight = dialog.findViewById(R.id.dialog_review_top_right);
    bottomLeft = dialog.findViewById(R.id.dialog_review_bottom_left);
    bottomRight = dialog.findViewById(R.id.dialog_review_bottom_right);

    return dialog;
  }

  @Override
  public void onFlicking(float verticalPercentage, float horizontalPercentage) {

    final float alpha = 1f - (Math.abs(verticalPercentage) + Math.abs(horizontalPercentage) / 1.4f);

    if(verticalPercentage < 0){
      if(horizontalPercentage < 0){
        topLeft.setAlpha(alpha);
        topRight.setAlpha(alpha);
        bottomRight.setAlpha(alpha);
      }else{
        topRight.setAlpha(alpha);
        bottomLeft.setAlpha(alpha);
        bottomRight.setAlpha(alpha);
      }
    }else{
      if(horizontalPercentage < 0){
        topLeft.setAlpha(alpha);
        topRight.setAlpha(alpha);
        bottomLeft.setAlpha(alpha);
      }else{
        topLeft.setAlpha(alpha);
        bottomLeft.setAlpha(alpha);
        bottomRight.setAlpha(alpha);
      }
    }
  }

  @Override
  public void onOriginBack() {
    topRight.setAlpha(1f);
    topLeft.setAlpha(1f);
    bottomLeft.setAlpha(1f);
    bottomRight.setAlpha(1f);
  }
}

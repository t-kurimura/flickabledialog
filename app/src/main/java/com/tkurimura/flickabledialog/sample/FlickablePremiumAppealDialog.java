package com.tkurimura.flickabledialog.sample;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;
import com.tkurimura.flickabledialog.FlickableDialogListener;

import static java.security.AccessController.getContext;

/**
 * Created by TakahisaKurimura on 2016/11/07.
 */

public class FlickablePremiumAppealDialog extends FlickableDialog {

  public static FlickablePremiumAppealDialog newInstance(Fragment fragment, FlickableDialogListener listener){

    FlickablePremiumAppealDialog flackablePremiumAppealDialog = new FlickablePremiumAppealDialog();
    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY,R.layout.dialog_premium_apple);
    flackablePremiumAppealDialog.setTargetFragment(fragment,0);
    flackablePremiumAppealDialog.setArguments(bundle);
    flackablePremiumAppealDialog.setFlickableDialogListener(listener);

    return flackablePremiumAppealDialog;
  }


  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);

    TextView butButton = (TextView) dialog.findViewById(R.id.dialog_premium_appeal_buy);
    butButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(getContext(),"Move to settlement page",Toast.LENGTH_SHORT).show();
        dismiss();
      }
    });

    return dialog;
  }
}

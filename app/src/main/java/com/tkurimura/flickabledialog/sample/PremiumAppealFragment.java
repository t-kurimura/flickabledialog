package com.tkurimura.flickabledialog.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;
import com.tkurimura.flickabledialog.FlickableDialogListener;

/**
 * Created by TakahisaKurimura on 2016/11/06.
 */

public class PremiumAppealFragment extends Fragment implements
    FlickableDialogListener {

  Switch switchView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_premium_appeal, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view,savedInstanceState);

    switchView = (Switch) view.findViewById(R.id.switch_premium);

    RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.switch_premium_holder);
    relativeLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
          switchView.setChecked(!switchView.isChecked());
      }
    });

    switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){

          FlickablePremiumAppealDialog flickablePremiumAppealDialog = FlickablePremiumAppealDialog.newInstance(
                  PremiumAppealFragment.this, PremiumAppealFragment.this);
          flickablePremiumAppealDialog.show(getChildFragmentManager(),FlickableDialog.class.getSimpleName());
        }
      }
    });
  }

  @Override public void onFlickableDialogFlicked(FlickableDialogListener.X_DIRECTION xDirection) {
    switchView.setChecked(false);
  }

  @Override
  public void onFlickableDialogCanceled() { }
}

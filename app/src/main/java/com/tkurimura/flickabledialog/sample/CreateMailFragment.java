package com.tkurimura.flickabledialog.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;

/**
 * Created by TakahisaKurimura on 2016/11/06.
 */

public class CreateMailFragment extends Fragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_create_mail, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view,savedInstanceState);

    final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        FlickableDialog dialog = FlickableDialog.newInstance(R.layout.dialog_create_mail);
        dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());
      }
    });
  }
}

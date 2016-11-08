package com.tkurimura.flickabledialog.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;

/**
 * Created by TakahisaKurimura on 2016/11/06.
 */

public class ReviewAppealFragment extends Fragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_review_appeal, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view,savedInstanceState);

    TextView nextButton = (TextView) view.findViewById(R.id.next_game_button);
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        FlickableDialog flickableDialog = FlickableDialog.newInstance(ReviewAppealFragment.this,R.layout.dialog_review_appeal);
        flickableDialog.show(getFragmentManager(),FlickableDialog.class.getSimpleName());
      }
    });
  }
}

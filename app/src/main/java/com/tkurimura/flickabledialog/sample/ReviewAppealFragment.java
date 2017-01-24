package com.tkurimura.flickabledialog.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;
import com.tkurimura.flickabledialog.FlickableDialogListener;

/**
 * Created by TakahisaKurimura on 2016/11/06.
 */

public class ReviewAppealFragment extends Fragment implements FlickableDialogListener {

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

        FlickableReviewAppealDialog flickableDialog = FlickableReviewAppealDialog.newInstance(ReviewAppealFragment.this);
        flickableDialog.show(getChildFragmentManager(),FlickableDialog.class.getSimpleName());
      }
    });
  }

  @Override public void onFlickableDialogFlicked(FlickableDialogListener.X_DIRECTION xDirection) {

    String reviewText;

    switch (xDirection){
      case LEFT_BOTTOM:
        reviewText = "Bad";
        break;
      case LEFT_TOP:
        reviewText = "Well";
        break;
      case RIGHT_BOTTOM:
        reviewText = "Good";
        break;
      case RIGHT_TOP:
        reviewText = "Great";
        break;
      default:
        reviewText = "unknown";
        break;
    }

    Toast.makeText(getContext(), "Appreciated to your ["+ reviewText +"]", Toast.LENGTH_SHORT).show();
  }

  @Override public void onFlickableDialogCanceled() { }
}

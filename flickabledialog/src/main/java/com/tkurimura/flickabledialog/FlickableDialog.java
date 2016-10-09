package com.tkurimura.flickabledialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by TakahisaKurimura on 2016/09/27.
 */

public class FlickableDialog extends DialogFragment {

    private static final String layoutResourceBundleKey = "layout_resource_bundle_key";

    private int oldx;

    private int oldy;

    public static FlickableDialog newInstance(@LayoutRes int layoutResources) {
        Bundle bundle = new Bundle();
        bundle.putInt(layoutResourceBundleKey, layoutResources);

        FlickableDialog flickableDialog = new FlickableDialog();
        flickableDialog.setArguments(bundle);
        return flickableDialog;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Bundle bundle = getArguments();
        @LayoutRes final int layoutResource = bundle.getInt(layoutResourceBundleKey);

        final View dialogView = LayoutInflater.from(getContext()).inflate(layoutResource, null);

        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {

                View rootView = dialogView.getRootView();

                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:

                        int left = rootView.getLeft() + (x - oldx);
                        int top = rootView.getTop() + (y - oldy);

                        rootView.layout(left, top, left + rootView.getWidth(), top
                            + rootView.getHeight());
                        break;
                }

                oldx = x;
                oldy = y;
                return true;
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        return builder.create();
    }
}
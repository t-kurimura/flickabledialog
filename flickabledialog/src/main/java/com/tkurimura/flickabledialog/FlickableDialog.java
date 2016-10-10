package com.tkurimura.flickabledialog;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class FlickableDialog extends DialogFragment {

  private static final String LAYOUT_RESOURCE_KEY = "layout_resource_bundle_key";

  private static final String ROTATE_ANIMATION_KEY = "rotate_animation_key";

  private static final String DISMISS_THRESHOLD_KEY = "layout_resource_bundle_key";

  private float DISMISS_THRESHOLD = 50f;

  private float ROTATE_ANIMATION = 30f;

  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  boolean touchedTopArea;

  private int previousX;

  private int previousY;

  public static FlickableDialog newInstance(@LayoutRes int layoutResources) {
    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY, layoutResources);

    FlickableDialog flickableDialog = new FlickableDialog();
    flickableDialog.setArguments(bundle);
    return flickableDialog;
  }

  public static FlickableDialog newInstance(@LayoutRes int layoutResources,
      float animationThreshold, float rotateAnimationAmount) {
    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY, layoutResources);

    if (animationThreshold != 0) {
      bundle.putFloat(DISMISS_THRESHOLD_KEY, animationThreshold);
    }
    if (rotateAnimationAmount != 0) {
      bundle.putFloat(ROTATE_ANIMATION_KEY, rotateAnimationAmount);
    }

    FlickableDialog flickableDialog = new FlickableDialog();
    flickableDialog.setArguments(bundle);
    return flickableDialog;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);

    Bundle bundle = getArguments();

    @LayoutRes final int layoutResource = bundle.getInt(LAYOUT_RESOURCE_KEY);

    DISMISS_THRESHOLD = bundle.getFloat(DISMISS_THRESHOLD_KEY, DISMISS_THRESHOLD);

    ROTATE_ANIMATION = bundle.getFloat(DISMISS_THRESHOLD_KEY, ROTATE_ANIMATION);

    final View dialogView = LayoutInflater.from(getContext()).inflate(layoutResource, null);

    compositeSubscription.add(
        Observable.create(new Observable.OnSubscribe<Pair<View, MotionEvent>>() {
          @Override public void call(final Subscriber<? super Pair<View, MotionEvent>> subscriber) {
            dialogView.setOnTouchListener(new View.OnTouchListener() {
              @Override public boolean onTouch(View v, MotionEvent event) {
                subscriber.onNext(Pair.create(v, event));
                return true;
              }
            });
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> viewMotionEventPair) {
            final View rootView = viewMotionEventPair.first.getRootView();
            final MotionEvent event = viewMotionEventPair.second;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
              touchedTopArea = isTouchStartedTop(rootView, event);
            }
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> pair) {
            if (pair.second.getAction() == MotionEvent.ACTION_MOVE) {

              final View rootView = pair.first.getRootView();

              final MotionEvent event = pair.second;

              int x = (int) event.getRawX();
              int y = (int) event.getRawY();

              int left = rootView.getLeft() + (x - previousX);
              int top = rootView.getTop() + (y - previousY);

              rootView.layout(left, top, left + rootView.getWidth(), top + rootView.getHeight());

              if (touchedTopArea) {
                rootView.setRotation(rootView.getX() / -ROTATE_ANIMATION);
              } else {
                rootView.setRotation(rootView.getX() / ROTATE_ANIMATION);
              }

              final float viewX = rootView.getX();
              final float viewY = rootView.getY();
              if (Math.abs(viewX) > DISMISS_THRESHOLD && Math.abs(viewY) > DISMISS_THRESHOLD) {

              }
            }
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> pair) {

            final MotionEvent event = pair.second;

            previousX = (int) event.getRawX();
            previousY = (int) event.getRawY();
          }
        }).filter(new Func1<Pair<View, MotionEvent>, Boolean>() {
          @Override public Boolean call(Pair<View, MotionEvent> pair) {
            return pair.second.getAction() == MotionEvent.ACTION_UP;
          }
        }).map(new Func1<Pair<View, MotionEvent>, View>() {
          @Override public View call(Pair<View, MotionEvent> viewMotionEventPair) {
            return viewMotionEventPair.first;
          }
        }).doOnNext(new Action1<View>() {
          @Override public void call(View view) {

            final View rootView = view.getRootView();

            int deltaX = 0 - rootView.getLeft();
            int deltaY = 0 - rootView.getTop();

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(rootView, "translationX", deltaX);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(rootView, "translationY", deltaY);
            ObjectAnimator animatorRotate =
                ObjectAnimator.ofFloat(rootView, "rotation", rootView.getRotation(), 0f);

            animatorX.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorY.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorRotate.setInterpolator(new AccelerateDecelerateInterpolator());

            animatorX.setDuration(400);
            animatorY.setDuration(400);
            animatorRotate.setDuration(400);

            animatorX.start();
            animatorY.start();
            animatorRotate.start();
          }
        }).subscribe());

    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(dialogView);
    return builder.create();
  }

  private boolean isTouchStartedTop(View rootView, MotionEvent event) {

    final float height = rootView.getHeight();

    final float initY = rootView.getY();

    final float eventRawY = event.getRawY();

    final float verticalCenter = initY + height / 2;

    return eventRawY < verticalCenter;
  }

  @Override public void onDismiss(DialogInterface dialogInterface) {
    compositeSubscription.unsubscribe();
    super.onDismiss(dialogInterface);
  }
}
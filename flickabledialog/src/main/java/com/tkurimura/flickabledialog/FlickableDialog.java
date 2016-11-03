package com.tkurimura.flickabledialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class FlickableDialog extends DialogFragment {

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

  private static final String LAYOUT_RESOURCE_KEY = "layout_resource_bundle_key";
  private static final String ROTATE_ANIMATION_KEY = "rotate_animation_key";
  private static final String DISMISS_THRESHOLD_KEY = "layout_resource_bundle_key";

  private float DISMISS_THRESHOLD = 700f;
  private float ROTATE_ANIMATION = 30f;

  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  boolean touchedTopArea;
  private int previousX;
  private int previousY;

  FlickableDialogListener.OnFlickedCross onFlickedCrossListener;
  FlickableDialogListener.OnOriginBack onOriginBackListener;
  FlickableDialogListener.OnFlicking onFlickingListener;

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    Object anyListener = getParentFragment();
    if (anyListener == null) {
      anyListener = getActivity();
      if (anyListener == null) {
        throw new IllegalStateException("cannot attach flickable dialog");
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
            // create touch event observable

            dialogView.setOnTouchListener(new View.OnTouchListener() {
              @Override public boolean onTouch(View v, MotionEvent event) {

                subscriber.onNext(Pair.create(v, event));

                return true;
              }
            });
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {

          @Override public void call(Pair<View, MotionEvent> viewMotionEventPair) {
            // memorize touched down position as member variables

            final View rootView = viewMotionEventPair.first.getRootView();
            final MotionEvent event = viewMotionEventPair.second;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

              final float height = rootView.getHeight();

              final float initY = rootView.getY();

              final float eventRawY = event.getRawY();

              final float verticalCenter = initY + height / 2;

              touchedTopArea = eventRawY < verticalCenter;
            }
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {
          // move view with finger and rotate view as touched down position
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
            }
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> pair) {
            // memorize previous position as member variables

            final MotionEvent event = pair.second;

            previousX = (int) event.getRawX();
            previousY = (int) event.getRawY();
          }
        }).filter(new Func1<Pair<View, MotionEvent>, Boolean>() {
          @Override public Boolean call(Pair<View, MotionEvent> pair) {
            return pair.second.getAction() == MotionEvent.ACTION_UP;
          }
        }).flatMap(new Func1<Pair<View, MotionEvent>, Observable<Pair<View, MotionEvent>>>() {
          // check delta amounts
          @Override public Observable<Pair<View, MotionEvent>> call(
              final Pair<View, MotionEvent> pair) {

            return Observable.just(pair).map(new Func1<Pair<View, MotionEvent>, View>() {
              @Override public View call(Pair<View, MotionEvent> pair) {

                return pair.first.getRootView();
              }
            }).map(new Func1<View, Pair<Integer, Integer>>() {
              // convert to delta amounts between origin and current position
              @Override public Pair<Integer, Integer> call(View rootView) {

                int deltaX;
                if (rootView.getLeft() < 0) {
                  deltaX = 0 - rootView.getLeft();
                } else {
                  deltaX = rootView.getWidth() - rootView.getRight();
                }

                int deltaY;
                if (rootView.getTop() < 0) {
                  deltaY = 0 - rootView.getTop();
                } else {
                  deltaY = rootView.getHeight() - rootView.getBottom();
                }

                return Pair.create(deltaX, deltaY);
              }
            }).doOnNext(new Action1<Pair<Integer, Integer>>() {
              // call back moved delta amount
              @Override public void call(Pair<Integer, Integer> deltaXYPair) {
                if (onFlickingListener != null) {

                  int percentageX = (int) (deltaXYPair.first / DISMISS_THRESHOLD);
                  int percentageY = (int) (deltaXYPair.second / DISMISS_THRESHOLD);

                  onFlickingListener.onFlicked(percentageX, percentageY);
                }
              }
            }).flatMap(new Func1<Pair<Integer, Integer>, Observable<Pair<View, MotionEvent>>>() {
              @Override
              public Observable<Pair<View, MotionEvent>> call(Pair<Integer, Integer> deltaXYPair) {
                // judge if flicking amount is over dismiss threshold
                if (Math.abs(deltaXYPair.first) > DISMISS_THRESHOLD
                    || Math.abs(deltaXYPair.second) > DISMISS_THRESHOLD) {

                  return Observable.just(deltaXYPair)
                      .map(new Func1<Pair<Integer, Integer>, Pair<View, MotionEvent>>() {
                        @Override public Pair<View, MotionEvent> call(
                            Pair<Integer, Integer> integerIntegerPair) {
                          return pair;
                        }
                      });
                } else {
                  // back to original dialog position with animation
                  return Observable.just(deltaXYPair)
                      .doOnNext(new Action1<Pair<Integer, Integer>>() {
                        @Override public void call(Pair<Integer, Integer> integerIntegerPair) {
                          final View rootView = pair.first.getRootView();

                          int deltaX = 0 - rootView.getLeft();
                          int deltaY = 0 - rootView.getTop();

                          PropertyValuesHolder horizontalAnimation =
                              PropertyValuesHolder.ofFloat("translationX", deltaX);
                          PropertyValuesHolder verticalAnimation =
                              PropertyValuesHolder.ofFloat("translationY", deltaY);
                          PropertyValuesHolder rotateAnimation =
                              PropertyValuesHolder.ofFloat("rotation", 0f);

                          ObjectAnimator originBackAnimation =
                              ObjectAnimator.ofPropertyValuesHolder(rootView, horizontalAnimation,
                                  verticalAnimation, rotateAnimation);

                          originBackAnimation.setInterpolator(
                              new AccelerateDecelerateInterpolator());

                          originBackAnimation.setDuration(300);

                          originBackAnimation.start();
                        }
                      })
                      .flatMap(new Func1<Pair<Integer, Integer>, Observable<?>>() {
                        @Override
                        public Observable<?> call(Pair<Integer, Integer> integerIntegerPair) {
                          return Observable.just(1)
                              .delay(300, TimeUnit.MILLISECONDS)
                              .observeOn(AndroidSchedulers.mainThread());
                        }
                      })
                      .doOnNext(new Action1<Object>() {
                        @Override public void call(Object o) {
                          if (onOriginBackListener != null) {
                            onOriginBackListener.onOriginBack();
                          }
                        }
                      })
                      .flatMap(new Func1<Object, Observable<Pair<View, MotionEvent>>>() {
                        @Override public Observable<Pair<View, MotionEvent>> call(Object o) {
                          return Observable.empty();
                        }
                      });
                }
              }
            });
          }
        }).flatMap(new Func1<Pair<View, MotionEvent>, Observable<Pair<View, MotionEvent>>>() {
          @Override
          public Observable<Pair<View, MotionEvent>> call(final Pair<View, MotionEvent> pair) {
            // create and start throwing animation
            return Observable.just(pair.first.getRootView())
                .map(new Func1<View, Pair<Integer, Integer>>() {
                  // convert to delta amounts between origin and current position
                  @Override public Pair<Integer, Integer> call(View rootView) {

                    int deltaX;
                    if (rootView.getLeft() < 0) {
                      deltaX = 0 - rootView.getLeft();
                    } else {
                      deltaX = rootView.getWidth() - rootView.getRight();
                    }

                    int deltaY;
                    if (rootView.getTop() < 0) {
                      deltaY = 0 - rootView.getTop();
                    } else {
                      deltaY = rootView.getHeight() - rootView.getBottom();
                    }

                    return Pair.create(deltaX, deltaY);
                  }
                })
                .map(new Func1<Pair<Integer, Integer>, Pair<View, MotionEvent>>() {
                  // create animation and start
                  @Override public Pair<View, MotionEvent> call(
                      Pair<Integer, Integer> integerIntegerPair) {

                    final View rootView = pair.first.getRootView();

                    PropertyValuesHolder horizontalAnimation =
                        PropertyValuesHolder.ofFloat("translationX",
                            -10 * integerIntegerPair.first);

                    PropertyValuesHolder verticalAnimation =
                        PropertyValuesHolder.ofFloat("translationY",
                            -10 * integerIntegerPair.second);

                    float rotation;
                    if (touchedTopArea) {
                      rotation = integerIntegerPair.first / DISMISS_THRESHOLD * 540f;
                    } else {
                      rotation = integerIntegerPair.first / DISMISS_THRESHOLD * -540f;
                    }
                    PropertyValuesHolder rotateAnimation =
                        PropertyValuesHolder.ofFloat("rotation", rotation);

                    ObjectAnimator originBackAnimation =
                        ObjectAnimator.ofPropertyValuesHolder(rootView, horizontalAnimation,
                            verticalAnimation, rotateAnimation);

                    originBackAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

                    originBackAnimation.setDuration(400);

                    originBackAnimation.start();
                    return pair;
                  }
                });
          }
        }).flatMap(new Func1<Pair<View, MotionEvent>, Observable<Pair<View, MotionEvent>>>() {
          // waiting animation end
          @Override public Observable<Pair<View, MotionEvent>> call(
              Pair<View, MotionEvent> viewMotionEventPair) {

            return Observable.just(viewMotionEventPair)
                .delay(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
          }
        }).flatMap(new Func1<Pair<View, MotionEvent>, Observable<Pair<View, MotionEvent>>>() {
          @Override public Observable<Pair<View, MotionEvent>> call(
              final Pair<View, MotionEvent> viewMotionEventPair) {

            return Observable.just(viewMotionEventPair.first.getRootView())
                .map(new Func1<View, Pair<Integer, Integer>>() {
                  // convert to delta amounts between origin and current position
                  @Override public Pair<Integer, Integer> call(View rootView) {

                    int deltaX;
                    if (rootView.getLeft() < 0) {
                      deltaX = 0 - rootView.getLeft();
                    } else {
                      deltaX = rootView.getWidth() - rootView.getRight();
                    }

                    int deltaY;
                    if (rootView.getTop() < 0) {
                      deltaY = 0 - rootView.getTop();
                    } else {
                      deltaY = rootView.getHeight() - rootView.getBottom();
                    }

                    return Pair.create(deltaX, deltaY);
                  }
                })
                .doOnNext(new Action1<Pair<Integer, Integer>>() {
                  // call back X direction
                  @Override public void call(Pair<Integer, Integer> integerIntegerPair) {
                    if (onFlickedCrossListener != null) {
                      if (integerIntegerPair.first < 0) {
                        if (integerIntegerPair.second < 0) {
                          onFlickedCrossListener.onFlicked(
                              FlickableDialogListener.X_DIRECTION.LEFT_BOTTOM);
                        } else {
                          onFlickedCrossListener.onFlicked(
                              FlickableDialogListener.X_DIRECTION.LEFT_TOP);
                        }
                      } else {
                        if (integerIntegerPair.second < 0) {
                          onFlickedCrossListener.onFlicked(
                              FlickableDialogListener.X_DIRECTION.RIGHT_BOTTOM);
                        } else {
                          onFlickedCrossListener.onFlicked(
                              FlickableDialogListener.X_DIRECTION.RIGHT_TOP);
                        }
                      }
                    }
                  }
                })
                .map(new Func1<Pair<Integer, Integer>, Pair<View, MotionEvent>>() {
                  @Override
                  public Pair<View, MotionEvent> call(Pair<Integer, Integer> integerIntegerPair) {
                    return viewMotionEventPair;
                  }
                });
          }
        }).subscribe(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> view) {
            dismiss();
          }
        }));

    Dialog dialog = new Dialog(getActivity());
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.setContentView(dialogView);

    return dialog;
  }

  @Override public void onDetach() {

    onFlickedCrossListener = null;
    onOriginBackListener = null;
    onFlickingListener = null;

    super.onDetach();
  }

  @Override public void onDismiss(DialogInterface dialogInterface) {

    compositeSubscription.unsubscribe();

    onFlickedCrossListener = null;
    onOriginBackListener = null;
    onFlickingListener = null;

    super.onDismiss(dialogInterface);
  }
}
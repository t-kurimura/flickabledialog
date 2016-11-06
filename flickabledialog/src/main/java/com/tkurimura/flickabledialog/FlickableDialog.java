package com.tkurimura.flickabledialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.util.Pair;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.subscriptions.CompositeSubscription;

public class FlickableDialog extends DialogFragment {

  protected static final String LAYOUT_RESOURCE_KEY = "layout_resource_bundle_key";
  protected static final String ROTATE_ANIMATION_KEY = "rotate_animation_key";
  protected static final String DISMISS_THRESHOLD_KEY = "layout_resource_bundle_key";
  protected static final String BACKGROUND_COLOR_RESOURCE_KEY = "color_resource_bundle_key";
  private boolean touchedTopArea;

  private float DISMISS_THRESHOLD = 700f;
  private float ROTATE_ANIMATION_EXPONENT = 30f;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private int previousX;
  private int previousY;
  private Integer defaultLeft;
  private Integer defaultTop;
  private boolean cancelAndDismissTaken = true;
  private boolean cancelable = false;
  private FlickableDialogListener flickableDialogListeners;

  public static FlickableDialog newInstance(@LayoutRes int layoutResources) {

    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY, layoutResources);

    FlickableDialog flickableDialog = new FlickableDialog();
    flickableDialog.setArguments(bundle);

    return flickableDialog;
  }

  public static FlickableDialog newInstance(Fragment fragment, @LayoutRes int layoutResources) {

    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY, layoutResources);

    FlickableDialog flickableDialog = new FlickableDialog();
    flickableDialog.setArguments(bundle);
    flickableDialog.setTargetFragment(fragment, 0);

    return flickableDialog;
  }

  public static FlickableDialog newInstance(@LayoutRes int layoutResources,
      float animationThreshold, float rotateAnimationAmount, @ColorRes int backgroundColor) {

    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY, layoutResources);

    if (animationThreshold != 0) {

      bundle.putFloat(DISMISS_THRESHOLD_KEY, animationThreshold);
    }
    if (rotateAnimationAmount != 0) {

      bundle.putFloat(ROTATE_ANIMATION_KEY, rotateAnimationAmount);
    }

    if (backgroundColor != 0) {

      bundle.putInt(BACKGROUND_COLOR_RESOURCE_KEY, backgroundColor);
    }

    FlickableDialog flickableDialog = new FlickableDialog();

    flickableDialog.setArguments(bundle);

    return flickableDialog;
  }

  public static FlickableDialog newInstance(Fragment fragment, @LayoutRes int layoutResources,
      float animationThreshold, float rotateAnimationAmount, @ColorRes int backgroundColor) {

    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY, layoutResources);

    if (animationThreshold != 0) {

      bundle.putFloat(DISMISS_THRESHOLD_KEY, animationThreshold);
    }
    if (rotateAnimationAmount != 0) {

      bundle.putFloat(ROTATE_ANIMATION_KEY, rotateAnimationAmount);
    }

    if (backgroundColor != 0) {

      bundle.putInt(BACKGROUND_COLOR_RESOURCE_KEY, backgroundColor);
    }

    FlickableDialog flickableDialog = new FlickableDialog();

    flickableDialog.setArguments(bundle);

    flickableDialog.setTargetFragment(fragment, 0);

    return flickableDialog;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    flickableDialogListeners = new FlickableDialogListener();
    flickableDialogListeners.holdListeners(getActivity());
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreateDialog(savedInstanceState);

    Bundle bundle = getArguments();

    @LayoutRes final int layoutResource = bundle.getInt(LAYOUT_RESOURCE_KEY);

    DISMISS_THRESHOLD = bundle.getFloat(DISMISS_THRESHOLD_KEY, DISMISS_THRESHOLD);
    ROTATE_ANIMATION_EXPONENT = bundle.getFloat(DISMISS_THRESHOLD_KEY, ROTATE_ANIMATION_EXPONENT);
    int backgroundColorResource = bundle.getInt(BACKGROUND_COLOR_RESOURCE_KEY, 0);

    final FrameLayout frameLayout = new FrameLayout(getContext());

    if (backgroundColorResource != 0) {
      frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColorResource));
    } else {
      frameLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));
    }

    compositeSubscription.add(Observable.create(new Observable.OnSubscribe<View>() {
      @Override public void call(final Subscriber<? super View> subscriber) {
        frameLayout.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            subscriber.onNext(v);
          }
        });
      }
    }).filter(new Func1<View, Boolean>() {
      @Override public Boolean call(View view) {
        return cancelAndDismissTaken;
      }
    }).map(new Func1<View, ObjectAnimator>() {
      @Override public ObjectAnimator call(View view) {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(frameLayout, "alpha", 1f, 0f);
        alphaAnimation.setDuration(300);

        return alphaAnimation;
      }
    }).flatMap(new Func1<ObjectAnimator, Observable<?>>() {
      @Override public Observable<?> call(ObjectAnimator objectAnimator) {

        objectAnimator.start();

        return Observable.just(1)
            .delay(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread());
      }
    }).subscribe(new Subscriber<Object>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(Object o) {
        dismiss();
      }
    }));

    compositeSubscription.add(
        Observable.create(new Observable.OnSubscribe<Pair<View, MotionEvent>>() {
          @Override public void call(final Subscriber<? super Pair<View, MotionEvent>> subscriber) {
            // create touch event observable

            final ViewGroup dialogView = (ViewGroup) LayoutInflater.from(getActivity())
                .inflate(layoutResource, frameLayout, true);

            dialogView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
              @Override public boolean onTouch(View v, MotionEvent event) {

                subscriber.onNext(Pair.create(v, event));
                return true;
              }
            });
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> viewMotionEventPair) {
            // memorize default content position as member variables
            if (defaultLeft == null || defaultTop == null) {
              // the first initial position
              defaultLeft = viewMotionEventPair.first.getLeft();
              defaultTop = viewMotionEventPair.first.getTop();
            }
          }
        }).doOnNext(new Action1<Pair<View, MotionEvent>>() {

          @Override public void call(Pair<View, MotionEvent> viewMotionEventPair) {
            // memorize touched down position as member variables
            final View rootView = viewMotionEventPair.first;
            final MotionEvent event = viewMotionEventPair.second;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

              final float height = rootView.getHeight();

              final float initY = rootView.getY();

              final float eventRawY = event.getRawY();

              final float verticalCenter = initY + height / 2;

              touchedTopArea = eventRawY < verticalCenter;
            }
          }
        }).flatMap(new Func1<Pair<View, MotionEvent>, Observable<Pair<View, MotionEvent>>>() {
          // move view with finger and rotate view as touched down position
          @Override public Observable<Pair<View, MotionEvent>> call(
              final Pair<View, MotionEvent> viewMotionEventPair) {

            return Observable.zip(Observable.just(viewMotionEventPair)
                .map(new Func1<Pair<View, MotionEvent>, Float>() {
                  @Override public Float call(Pair<View, MotionEvent> viewMotionEventPair) {

                    return (float) (viewMotionEventPair.first.getLeft() - defaultLeft);
                  }
                }), Observable.just(viewMotionEventPair)
                .map(new Func1<Pair<View, MotionEvent>, Pair<Integer, Integer>>() {
                  @Override
                  public Pair<Integer, Integer> call(Pair<View, MotionEvent> viewMotionEventPair) {

                    int currentX = (int) viewMotionEventPair.second.getRawX();
                    int currentY = (int) viewMotionEventPair.second.getRawY();

                    final int left = viewMotionEventPair.first.getLeft() + (currentX - previousX);
                    final int top = viewMotionEventPair.first.getTop() + (currentY - previousY);

                    return Pair.create(left, top);
                  }
                }), new Func2<Float, Pair<Integer, Integer>, Pair<View, MotionEvent>>() {
              @Override public Pair<View, MotionEvent> call(Float verticalGap,
                  Pair<Integer, Integer> leftTopPair) {
                if (viewMotionEventPair.second.getAction() == MotionEvent.ACTION_MOVE) {
                  // rotation
                  if (touchedTopArea) {
                    viewMotionEventPair.first.setRotation(verticalGap / -ROTATE_ANIMATION_EXPONENT);
                  } else {
                    viewMotionEventPair.first.setRotation(verticalGap / ROTATE_ANIMATION_EXPONENT);
                  }

                  // position
                  View rootView = viewMotionEventPair.first;
                  rootView.layout(leftTopPair.first, leftTopPair.second,
                      leftTopPair.first + rootView.getWidth(),
                      leftTopPair.second + rootView.getHeight());
                }
                return viewMotionEventPair;
              }
            });
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
                return pair.first;
              }
            }).map(new Func1<View, Pair<Integer, Integer>>() {
              // convert to delta amounts between origin and current position
              @Override public Pair<Integer, Integer> call(View rootView) {

                int deltaX = defaultLeft - rootView.getLeft();
                int deltaY = defaultTop - rootView.getTop();

                return Pair.create(deltaX, deltaY);
              }
            }).doOnNext(new Action1<Pair<Integer, Integer>>() {
              // call back moved delta amount
              @Override public void call(Pair<Integer, Integer> deltaXYPair) {
                FlickableDialogListener.OnFlicking onFlickingListener =
                    flickableDialogListeners.getOnFlickingListener(FlickableDialog.this);
                if (onFlickingListener != null) {

                  int percentageX = (int) (deltaXYPair.first / DISMISS_THRESHOLD);
                  int percentageY = (int) (deltaXYPair.second / DISMISS_THRESHOLD);

                  onFlickingListener.onFlicked(percentageX, percentageY);
                }
              }
            }).flatMap(new Func1<Pair<Integer, Integer>, Observable<Pair<View, MotionEvent>>>() {
              @Override public Observable<Pair<View, MotionEvent>> call(
                  final Pair<Integer, Integer> deltaXYPair) {
                // judge if flicking amount is over dismiss threshold
                if (Math.abs(deltaXYPair.first) > DISMISS_THRESHOLD
                    || Math.abs(deltaXYPair.second) > DISMISS_THRESHOLD) {
                  // flicking amount is over threshold
                  // -> streams go below to animate throwing
                  return Observable.just(deltaXYPair)
                      .map(new Func1<Pair<Integer, Integer>, Pair<View, MotionEvent>>() {
                        @Override public Pair<View, MotionEvent> call(
                            Pair<Integer, Integer> integerIntegerPair) {
                          return pair;
                        }
                      });
                } else {
                  // back to original dialog position with animation
                  // -> streams is terminated with animate back to origin
                  final int originBackAnimationDuration = 300;

                  return Observable.just(deltaXYPair)
                      .doOnNext(new Action1<Pair<Integer, Integer>>() {
                        @Override public void call(Pair<Integer, Integer> deltaXYPair) {

                          PropertyValuesHolder horizontalAnimation =
                              PropertyValuesHolder.ofFloat("translationX", deltaXYPair.first);
                          PropertyValuesHolder verticalAnimation =
                              PropertyValuesHolder.ofFloat("translationY", deltaXYPair.second);
                          PropertyValuesHolder rotateAnimation =
                              PropertyValuesHolder.ofFloat("rotation", 0f);

                          ObjectAnimator originBackAnimation =
                              ObjectAnimator.ofPropertyValuesHolder(pair.first, horizontalAnimation,
                                  verticalAnimation, rotateAnimation);

                          originBackAnimation.setInterpolator(
                              new AccelerateDecelerateInterpolator());

                          originBackAnimation.setDuration(originBackAnimationDuration);

                          originBackAnimation.start();
                        }
                      })
                      .flatMap(new Func1<Pair<Integer, Integer>, Observable<?>>() {
                        @Override
                        public Observable<?> call(Pair<Integer, Integer> integerIntegerPair) {
                          return Observable.just(1)
                              .delay(originBackAnimationDuration, TimeUnit.MILLISECONDS)
                              .observeOn(AndroidSchedulers.mainThread());
                        }
                      })
                      .doOnNext(new Action1<Object>() {
                        @Override public void call(Object o) {
                          FlickableDialogListener.OnOriginBack onOriginBackListener =
                              flickableDialogListeners.getOnOriginBackListener(
                                  FlickableDialog.this);
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
            return Observable.just(pair.first).map(new Func1<View, Pair<Integer, Integer>>() {
              // convert to delta amounts between origin and current position
              @Override public Pair<Integer, Integer> call(View rootView) {

                int deltaX = defaultLeft - rootView.getLeft();
                int deltaY = defaultTop - rootView.getTop();

                return Pair.create(deltaX, deltaY);
              }
            }).flatMap(new Func1<Pair<Integer, Integer>, Observable<Pair<View, MotionEvent>>>() {
              @Override public Observable<Pair<View, MotionEvent>> call(
                  Pair<Integer, Integer> integerIntegerPair) {
                // make and start throwing animation
                return Observable.zip(Observable.just(integerIntegerPair)
                        .map(new Func1<Pair<Integer, Integer>, PropertyValuesHolder>() {
                          @Override
                          public PropertyValuesHolder call(Pair<Integer, Integer> deltaXYPair) {
                            // make rotate animation
                            float rotation;
                            if (touchedTopArea) {
                              rotation = deltaXYPair.first / DISMISS_THRESHOLD * 540f;
                            } else {
                              rotation = deltaXYPair.first / DISMISS_THRESHOLD * -540f;
                            }
                            PropertyValuesHolder rotateAnimation =
                                PropertyValuesHolder.ofFloat("rotation", rotation);

                            return rotateAnimation;
                          }
                        }), Observable.just(integerIntegerPair)
                        .map(
                            new Func1<Pair<Integer, Integer>, Pair<PropertyValuesHolder, PropertyValuesHolder>>() {
                              @Override public Pair<PropertyValuesHolder, PropertyValuesHolder> call(
                                  Pair<Integer, Integer> deltaXYPair) {
                                // make position transit animation
                                PropertyValuesHolder horizontalAnimation =
                                    PropertyValuesHolder.ofFloat("translationX",
                                        -10 * deltaXYPair.first);

                                PropertyValuesHolder verticalAnimation =
                                    PropertyValuesHolder.ofFloat("translationY",
                                        -10 * deltaXYPair.second);

                                return Pair.create(horizontalAnimation, verticalAnimation);
                              }
                            }), Observable.just(integerIntegerPair)
                        .map(new Func1<Pair<Integer, Integer>, ObjectAnimator>() {
                          @Override
                          public ObjectAnimator call(Pair<Integer, Integer> integerIntegerPair) {
                            // make background alpha transit animation
                            ObjectAnimator alphaAnimation =
                                ObjectAnimator.ofFloat(pair.first.getRootView(), "alpha", 1f, 0f);
                            alphaAnimation.setDuration(400);

                            return alphaAnimation;
                          }
                        }),
                    new Func3<PropertyValuesHolder, Pair<PropertyValuesHolder, PropertyValuesHolder>, ObjectAnimator, Pair<View, MotionEvent>>() {
                      @Override
                      public Pair<View, MotionEvent> call(PropertyValuesHolder propertyValuesHolder,
                          Pair<PropertyValuesHolder, PropertyValuesHolder> propertyValuesHolderPropertyValuesHolderPair,
                          ObjectAnimator alphaAnimation) {
                        // zip and do animation

                        ObjectAnimator throwingAnimation =
                            ObjectAnimator.ofPropertyValuesHolder(pair.first, propertyValuesHolder,
                                propertyValuesHolderPropertyValuesHolderPair.first,
                                propertyValuesHolderPropertyValuesHolderPair.second);
                        throwingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                        throwingAnimation.setDuration(400);

                        alphaAnimation.setDuration(400);

                        throwingAnimation.start();
                        alphaAnimation.start();

                        return pair;
                      }
                    });
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

            return Observable.just(viewMotionEventPair.first)
                .map(new Func1<View, Pair<Integer, Integer>>() {
                  // convert to delta amounts between origin and current position
                  @Override public Pair<Integer, Integer> call(View rootView) {

                    int deltaX = defaultLeft - rootView.getLeft();
                    int deltaY = defaultTop - rootView.getTop();

                    return Pair.create(deltaX, deltaY);
                  }
                })
                .doOnNext(new Action1<Pair<Integer, Integer>>() {
                  // call back X direction
                  @Override public void call(Pair<Integer, Integer> integerIntegerPair) {
                    FlickableDialogListener.OnFlickedCross onFlickedCrossListener =
                        flickableDialogListeners.getOnFlickedCrossListener(FlickableDialog.this);
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
        }).doOnSubscribe(new Action0() {
          @Override public void call() {

            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(frameLayout, "alpha", 0f, 1f);
            alphaAnimation.setDuration(200);
            alphaAnimation.start();
          }
        }).subscribe(new Action1<Pair<View, MotionEvent>>() {
          @Override public void call(Pair<View, MotionEvent> view) {
            dismiss();
          }
        }));

    Dialog dialog =
        new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    dialog.setContentView(frameLayout);
    dialog.setCancelable(cancelable);

    return dialog;
  }

  public void setCanceledOnTouchOutside(boolean cancel) {
    this.cancelAndDismissTaken = cancel;
  }

  public void setCancelable(boolean flag) {
    this.cancelable = flag;
  }

  @Override public void onDetach() {

    compositeSubscription.unsubscribe();

    flickableDialogListeners.destroyListeners();

    super.onDetach();
  }

  @Override public void onDismiss(DialogInterface dialogInterface) {

    compositeSubscription.unsubscribe();

    flickableDialogListeners.destroyListeners();

    super.onDismiss(dialogInterface);
  }
}
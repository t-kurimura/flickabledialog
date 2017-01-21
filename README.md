# FlickableDialog
	
This dialog can flick and make it easy to dismiss sensuously.
You can show your information to users with minimum stress.
And, users can swipe dialog to select options extremely easy!

## Download 

Gradle : 

```
compile 'com.tkurimura:flickabledialog:1.0.0'
```

## Require

Java7 and Android minimum API level(SDK) 11 (Android 3.0)

## Usecase demo 

|AlertDialog|Premium appeal|
|---|---|
|![AlertDialog](https://github.com/t-kurimura/flickabledialog/blob/master/alert_dialog.gif)|![Premium](https://github.com/t-kurimura/flickabledialog/blob/master/premium_appeal.gif)|
|Profile setting|Review popup|
|in develop|![Review](https://github.com/t-kurimura/flickabledialog/blob/master/review_popup.gif)|


## Usage

### Show

* Call from Activity

Attention to use `getSupportFragmentManager()` as argument in dialog.show(,);

```java
// simple way to call
FlickableDialog dialog = FlickableDialog.newInstance(R.layout.your_dialog_layout);
dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());
```

```java

// you can define detail flicking settings 
FlickableDialog dialog = FlickableDialog.newInstance(
	R.layout.your_dialog_layout, // dialog content layout resource Id
	800f,                        // the area raidus dialog will come back to original position (default : 700f)
	50f,                         // slope when you flick dialog to side direction (default : 30f)
	R.color.colorAccent,         // background color of the area where dialog dismiss if you touch
	flickableListener);          // listener for flick/cancel actions

dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());

```

* Call from Fragment

Attention to use `getChildFragmentManager()` as argument in dialog.show(,);

```java
// simple way to call
FlickableDialog dialog = FlickableDialog.newInstance(R.layout.your_dialog_layout);
dialog.show(getChildFragmentManager(), dialog.getClass().getSimpleName());
```

```java
// you can define detail flicking settings 
FlickableDialog dialog = FlickableDialog.newInstance(
	R.layout.your_dialog_layout, // dialog content layout resource Id
	800f,                        // the area raidus dialog will come back to original position (default : 700f)
	50f,                         // slope when you flick dialog to side direction (default : 30f)
	R.color.colorAccent,         // background color of the area where dialog dismiss if you touch
	flickableListener);          // listener for flick/cancel actions

dialog.show(getChildFragmentManager(), dialog.getClass().getSimpleName());
```

### Callback

Provide an object that implements `FlickableDialogListener`.

```java
HogeActivity extend Activity implement FlickableDialogListener {

    // On flicked
    @Override
    public void onFlickableDialogFlicked(FlickableDialogListener.X_DIRECTION xDirection) {
        // do something according to flicked direction
        if(xDirection.equals(LEFT_BOTTOM)){
            doSomething();
        }
    }
    // invoked when user touches outside of the dialog
    @Override
    public void onFlickableDialogCanceled() {
        getActivity().finish();
    }
}
```

### Custom

You can extend FlickableDialog to your custom dialog.

```java
public class FlickableHogeDialog extends FlickableDialog {

  public static FlickableHogeDialog newInstance(Fragment fragment, FlickableDialogListener listener){

    FlickableHogeDialog flickableHogeDialog = new FlickableHogeDialog();
    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_RESOURCE_KEY,R.layout.your_custom_layout);
    flickableHogeDialog.setTargetFragment(fragment,0);
    flickableHogeDialog.setArguments(bundle);
    flickableHogeDialog.setFlickableDialogListener(listener);

    return flickableHogeDialog;
  }

  @Override 
  public Dialog onCreateDialog(Bundle savedInstanceState) {
  Dialog dialog = super.onCreateDialog(savedInstanceState);
  
  Button button = (Button) dialog.findViewById(R.id.your_custom_complete_button);
  button.setOnClickListener(new View.OnClickListener() {
    @Override 
    public void onClick(View v) {
      Toast.makeText(getContext(),"You tapped Complete button!",Toast.LENGTH_SHORT).show();
      dismiss();
    }
  });
  
    return dialog;
  }

  @Override
  public void onFlicking(float verticalPercentage, float horizontalPercentage) {
  	// callback when dialog moves according to flicks
  	changeStatus();
  }

  @Override
  public void onOriginBack() {
  	// when dialog position came back to default position
  }
```


## License

```
The MIT License (MIT)
Copyright (c) 2016 Takahisa Kurimura

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

```

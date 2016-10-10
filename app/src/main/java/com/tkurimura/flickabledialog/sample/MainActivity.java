package com.tkurimura.flickabledialog.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.sample.R;
import com.tkurimura.flickabledialog.FlickableDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlickableDialog dialog = FlickableDialog.newInstance(R.layout.dialog_sample);
                dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
            }
        });
    }
}
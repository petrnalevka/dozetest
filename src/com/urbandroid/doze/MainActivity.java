package com.urbandroid.doze;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.urbandroid.common.error.ErrorReporter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        findViewById(R.id.log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog onDemandReportDialog = ErrorReporter.getInstance().provideOnDemandDialog(MainActivity.this);
                onDemandReportDialog.show();
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), DozeService.class));
            }
        });

        startService(new Intent(getApplicationContext(), DozeService.class));
    }


}

package com.eun.percentify;

import android.app.Activity;
import android.os.Bundle;

public class EmptyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

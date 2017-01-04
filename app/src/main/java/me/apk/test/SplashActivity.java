package me.apk.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import me.apk.container.HostEntryActivity;

public class SplashActivity extends HostEntryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    public void onBugAc(View view) {
        startActivity(new Intent(this,HasBugActivity.class));
    }
}

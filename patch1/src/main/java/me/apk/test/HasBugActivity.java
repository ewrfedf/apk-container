package me.apk.test;

import android.os.Bundle;

import me.apk.patch.PatchBaseActivity;
import me.apk.patch1.R;

public class HasBugActivity extends PatchBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_has_bug);
    }
}

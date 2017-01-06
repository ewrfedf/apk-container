package me.apk.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import me.apk.patch.PatchBaseActivity;
import me.apk.patch1.R;

public class HasBugActivity extends PatchBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patch_activity_has_bug);
    }

    public void onNewAc(View view) {
        startActivity(new Intent(this, NewPatchActivity.class));
    }
}

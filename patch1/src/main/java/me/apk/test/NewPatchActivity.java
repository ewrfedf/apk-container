package me.apk.test;

import android.os.Bundle;
import android.widget.Toast;

import me.apk.patch.PatchBaseActivity;
import me.apk.patch1.R;

public class NewPatchActivity extends PatchBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"hello Hook",Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_new_patch);
    }
}

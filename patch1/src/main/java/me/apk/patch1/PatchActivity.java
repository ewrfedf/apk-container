package me.apk.patch1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PatchActivity extends PatchB{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

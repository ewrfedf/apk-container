package me.apk.patch;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by Zheng on 17/1/3.
 */

public class PatchBaseActivity extends Activity {
    private AssetManager mAssetManager;
    private Resources mResources;
    private String mDexPath;
    private Resources.Theme mTheme;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            File dexFile = getFileStreamPath("Patch.apk");
            mDexPath = dexFile.getPath();
        } catch (Throwable throwable) {
            throw new RuntimeException("hook failed", throwable);
        }
        loadResources();
    }


    protected void loadResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
         mTheme = mResources.newTheme();
         mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }
}

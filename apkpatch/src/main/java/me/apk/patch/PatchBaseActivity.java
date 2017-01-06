package me.apk.patch;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Patch Apk 中的 Activity 必须要继承该 Activity
 * 该 Activity 可以加载 Patch Apk 中的资源
 * 否则 UI 将因不能找到资源显示空白页
 */
public class PatchBaseActivity extends Activity {
    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        loadResources();
    }


    /**
     * 获取 Host 中已经加载好的 Patch Apk 中的 Resources
     */
    protected void loadResources() {
        // 宿主 已经将当前 Patch Apk 加载到程序私有目录下 名称必须一致为 "patch.apk"
        File dexFile = getFileStreamPath("patch.apk");
        String mDexPath = dexFile.getPath();
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

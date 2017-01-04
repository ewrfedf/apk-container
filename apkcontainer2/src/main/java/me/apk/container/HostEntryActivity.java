package me.apk.container;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import me.apk.container.hook.AMSHookHelper;
import me.apk.container.hook.BaseDexClassLoaderHookHelper;
import me.apk.container.hook.Utils;

/**
 * 1、下载 patch apk
 * 2、load patch apk
 * 3、跳入指定 Activity 以及 携带的参数
 */
public abstract class HostEntryActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        hook("");
    }

    /**
     *
     * @param patchPath 需要加载的 apk
     */
    private void hook(String patchPath) {
        try {
            File dexFile = getFileStreamPath("Patch.apk");
            if (!dexFile.exists()) {
                Utils.extractAssets(this, "Patch.apk");
            }
            File optDexFile = getFileStreamPath("xx.odex");//名称任意 非 apk
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);
            BaseDexClassLoaderHookHelper.patchResourceLoader(null,
                    null, getApplicationContext(), dexFile.getPath());
            AMSHookHelper.hookActivityManagerNative();
            AMSHookHelper.hookActivityThreadHandler();
        } catch (Throwable throwable) {
            throw new RuntimeException("hook failed", throwable);
        }
    }


}

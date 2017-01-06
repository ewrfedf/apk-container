package me.apk.container;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import me.apk.container.hook.AMSHookHelper;
import me.apk.container.hook.BaseDexClassLoaderHookHelper;

/**
 *
 * Hook 加载 Class
 * Hook 加载未声明的 Activity
 *
 * 如果已经 Load Apk ，那么直接在 attachBaseContext 中 Hook
 *
 */
public abstract class HostEntryActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        File dexFile = getFileStreamPath("patch.apk");
        if (dexFile.exists()) {//如果已经升级 直接加载
            hookForLoadClass();
            hookForLoadUndefineActivity();
        }
    }

    /**
     * 加载 class
     */
    protected void hookForLoadClass() {
        try {
            File dexFile = getFileStreamPath("patch.apk");
            File optDexFile = getFileStreamPath("xx.odex");//名称任意 非 apk
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);
        } catch (Throwable throwable) {
            throw new RuntimeException("hookForLoadClass failed", throwable);
        }
    }

    /**
     * 使 Patch Apk 可以加载 Host Apk 中 Androidminifest 文件中未声明的 Activity
     */
    protected void hookForLoadUndefineActivity() {
        try {
            AMSHookHelper.hookActivityManagerNative();
            AMSHookHelper.hookActivityThreadHandler();
        } catch (Throwable throwable) {
            throw new RuntimeException("hookForLoadClass failed", throwable);
        }

    }


}

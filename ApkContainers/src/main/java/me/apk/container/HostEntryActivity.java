package me.apk.container;

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

    /**
     * 加载 class
     * @param patchPath 需要加载的 apk
     */
    protected void hookForLoadClass(String patchPath) {
        try {
            //apk 文件需要放到程序目录下 否则资源加载不成功
            File dexFile = getFileStreamPath("patch.apk");
            String path = dexFile.getPath();
            if (!dexFile.exists()) {
                Utils.copyFile(patchPath, path);
//                可以使用 Assets 中的 Apk 进行测试
//                Utils.extractAssets(this, "patch.apk");
            }
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

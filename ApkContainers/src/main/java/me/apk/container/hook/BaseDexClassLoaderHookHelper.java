package me.apk.container.hook;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

import static android.content.ContentValues.TAG;

/**
 * 适配 Bubble OS
 */
public final class BaseDexClassLoaderHookHelper {

    /**
     *  加载 class
     * @param cl
     * @param apkFile
     * @param optDexFile
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile)
            throws IllegalAccessException, NoSuchMethodException, IOException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        // 获取 BaseDexClassLoader : pathList
        Field pathListField = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
        pathListField.setAccessible(true);
        Object pathListObj = pathListField.get(cl);

        // 获取 PathList: Element[] dexElements
        Field dexElementArray = pathListObj.getClass().getDeclaredField("dexElements");
        dexElementArray.setAccessible(true);
        Object[] dexElements = (Object[]) dexElementArray.get(pathListObj);

        // Element 类型
        Class<?> elementClass = dexElements.getClass().getComponentType();

        // 创建一个数组, 用来替换原始的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);

        // 构造插件 Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
        Constructor<?> constructor;
        Object o;
        try {
            constructor = elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);
            o = constructor.newInstance(apkFile, false, apkFile,
                    DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
        } catch (NoSuchMethodException e) {// 支持 B_OS
            constructor = elementClass.getConstructor(File.class, File.class, DexFile.class);
            String absolutePath = optDexFile.getAbsolutePath();
            String canonicalPath = apkFile.getCanonicalPath();
            DexFile dexFile = DexFile.loadDex(canonicalPath, absolutePath, 0);
            o = constructor.newInstance(apkFile, apkFile,
                    dexFile);
        }

        Object[] toAddElementArray = new Object[]{o};
        //  两个类的全限定名一致，Patch 覆盖 host apk 中的 class，优先加载，优先使用。
        System.arraycopy(toAddElementArray, 0, newElements, 0, toAddElementArray.length);
        // 把原始的elements复制进去
        System.arraycopy(dexElements, 0, newElements, toAddElementArray.length, dexElements.length);

        Log.i(TAG, "patchClassLoader: toAddElementArray------>" + Arrays.deepToString(toAddElementArray));
        Log.i(TAG, "patchClassLoader: dexElements------>" + Arrays.deepToString(dexElements));

        // 替换
        dexElementArray.set(pathListObj, newElements);

    }



    /**
     * 加载资源
     *
     * @param ctx
     * @param mDexPath
     */
    public static void patchResourceLoader(
            AssetManager mAssetManager, Resources mResources,
            Context ctx, String mDexPath) {
        Resources.Theme mTheme;
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = ctx.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(ctx.getTheme());
    }
}

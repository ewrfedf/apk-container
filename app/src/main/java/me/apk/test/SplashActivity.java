package me.apk.test;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import me.apk.container.HostEntryActivity;
import me.apk.container.hook.Utils;

public class SplashActivity extends HostEntryActivity {

    private DownloadManager mDownloadManager;
    private long enqueueId;
    private BroadcastReceiver mBroadcastReceiver;
    String apkDownloadUrl =
            "https://s.beta.myapp.com/myapp/rdmexp/exp/file/meapkpatch1_10_1b79b7b2-b5d6-44d1-8607-ab8fd2327ab8.apk";
    private boolean isRegDMR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkHotFix();
    }


    /**
     * 检测是否已经升级
     */
    private void checkHotFix() {
        File dexFile = getFileStreamPath("patch.apk");
        if (!dexFile.exists()) {
            registerDM();
            downloadNewVersion(apkDownloadUrl);
        }
    }

    /**
     * 注册下载监听
     */
    private void registerDM() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadCompletedId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                // 检查是否是自己的下载队列 id, 有可能是其他应用的
                if (enqueueId != downloadCompletedId) {
                    return;
                }
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(enqueueId);
                Cursor c = mDownloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    // 下载失败也会返回这个广播，所以要判断下是否真的下载成功
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        // 获取下载好的 apk 路径
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        //将下载好的 Apk 放到程序私有目录中进行 Hook
                        File dexFile = getFileStreamPath("patch.apk");
                        String path = dexFile.getPath();
                        if (!dexFile.exists()) {
                            Utils.copyFile(uriString, path);
                        }
                        // Hook
                        hookForLoadClass();
                        hookForLoadUndefineActivity();
                    }
                }
            }
        };
        // 注册广播, 设置只接受下载完成的广播
        isRegDMR = true;
        registerReceiver(mBroadcastReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    /**
     * 启动下载器
     *
     * @param apkDownloadUrl
     */
    private void downloadNewVersion(String apkDownloadUrl) {
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkDownloadUrl));
        enqueueId = mDownloadManager.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegDMR) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    public void onBugAc(View view) {
        startActivity(new Intent(this, HasBugActivity.class));
    }
}

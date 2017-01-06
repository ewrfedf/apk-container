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

import me.apk.container.HostEntryActivity;

public class SplashActivity extends HostEntryActivity {

    private DownloadManager mDownloadManager;
    private long enqueueId;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean needDownload = true;
    String apkDownloadUrl =
            "https://s.beta.myapp.com/myapp/rdmexp/exp/file/meapkpatch1_10_1d0c5c0c-fa52-44eb-8104-b49a555d797e.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkHotFix();
    }

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
                        hookForLoadClass(uriString);
                        hookForLoadUndefineActivity();
                    }
                }
            }
        };
        // 注册广播, 设置只接受下载完成的广播
        registerReceiver(mBroadcastReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void checkHotFix() {
        if (needDownload) {
            registerDM();
            downloadNewVersion(apkDownloadUrl);
        }
    }

    public void downloadNewVersion(String apkDownloadUrl) {
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // apkDownloadUrl 是 apk 的下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkDownloadUrl));
        // 获取下载队列 id
        enqueueId = mDownloadManager.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void onBugAc(View view) {
        startActivity(new Intent(this, HasBugActivity.class));
    }
}

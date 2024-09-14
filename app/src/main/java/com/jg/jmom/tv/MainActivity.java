package com.gecko.webview.tv;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gecko.webview.tv.pop.InputPop;
import com.gecko.webview.tv.utils.QRCodeEncoder;
import com.gecko.webview.tv.utils.SPUtil;
import com.gecko.webview.tv.utils.Util;
import com.lxj.xpopup.XPopup;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends AppCompatActivity {
    private static GeckoRuntime sRuntime;
    private GeckoSession geckoSession;
    private RelativeLayout progressLayout;
    private HttpServer httpServer;
    private TextView tvHint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHttpServer();
        registerNetworkReceiver();
        fullScreen();
        initView();
        if (SPUtil.getBoolean(this, "isUrlConfigured", false)) {
            initWebView(SPUtil.getString(this, "url", ""));
            tvHint.setVisibility(View.GONE);
        } else {
            tvHint.setVisibility(View.VISIBLE);
            showPop();
        }
    }

    private void startHttpServer() {
        try {
            httpServer = new HttpServer();
            httpServer.start();
            httpServer.setListener(new HttpServer.OnDataListener() {
                @Override
                public void onData(String input) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (inputPop != null && inputPop.isShow()){
                                inputPop.setInput(input);
                            }else {
                                handleInput(input);
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWebView(String url) {
        GeckoView view = findViewById(R.id.geckoView);
        geckoSession = new GeckoSession();
        // Workaround for Bug 1758212
        geckoSession.setContentDelegate(new GeckoSession.ContentDelegate() {
        });
        if (sRuntime == null) {
            // GeckoRuntime can only be initialized once per process
            sRuntime = GeckoRuntime.create(this);
        }
        geckoSession.open(sRuntime);
        view.setSession(geckoSession);

        geckoSession.setProgressDelegate(new GeckoSession.ProgressDelegate() {
            @Override
            public void onPageStart(GeckoSession session, String url) {
                // 页面开始加载时调用
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStop(GeckoSession session, boolean success) {
                // 页面加载完成时调用
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChange(GeckoSession session, int progress) {
                // 页面加载进度变化时调用
            }
        });
        loadUrl(url);
    }

    private void loadUrl(String url) {
        geckoSession.loadUri(url);
    }

    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private InputPop inputPop;
    private void showPop() {
        String ip = Util.getIPAddress(true);
        String address = "http://" + ip + ":8080";
        Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(address, 500);
        inputPop = (InputPop) new XPopup.Builder(this)
                .isDestroyOnDismiss(true)
                .asCustom(new InputPop(this, address, bitmap, new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String input) {
                       handleInput(input);
                    }
                }))
                .show();
    }

    private void handleInput(String input){
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(MainActivity.this, "地址不能是空", Toast.LENGTH_SHORT).show();
            return;
        }
        tvHint.setVisibility(View.GONE);
        if (geckoSession != null && geckoSession.isOpen()) {
            loadUrl(input);
        } else {
            initWebView(input);
        }
        SPUtil.saveString(MainActivity.this, "url", input);
        SPUtil.saveBoolean(MainActivity.this, "isUrlConfigured", true);

        SPUtil.saveInputHistory(this,input);
    }

    // 记录点击次数
    private int clickCount = 0;
    // 记录每次点击的时间
    private long firstClickTime = 0;
    private long secondClickTime = 0;

    private void initView() {
        tvHint = findViewById(R.id.tv_hint);
        findViewById(R.id.view_click).setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (clickCount == 0) {
                firstClickTime = currentTime;
                clickCount++;
            } else if (clickCount == 1) {
                secondClickTime = currentTime;
                if (secondClickTime - firstClickTime <= 1000) {
                    clickCount++;
                } else {
                    clickCount = 1;
                    firstClickTime = currentTime;
                }
            }
            // 第三次点击
            else if (clickCount == 2) {
                if (currentTime - firstClickTime <= 1000) {
                    showPop();
                    clickCount = 0;
                } else {
                    clickCount = 1;
                    firstClickTime = currentTime;
                }
            }
        });

        progressLayout = findViewById(R.id.progress_layout);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            showPop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
        }
        if (geckoSession != null) {
            geckoSession.close();
            geckoSession = null;
        }
    }

    private NetworkChangeReceiver networkChangeReceiver;

    private void registerNetworkReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Util.isNetworkAvailable(context)) {
                reloadPage();
            } else {
                Toast.makeText(MainActivity.this, "连接已断开, 请检查网络配置", Toast.LENGTH_SHORT).show();
                registerNetworkCallback(context);
            }
        }

        private void registerNetworkCallback(final Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(android.net.Network network) {
                        super.onAvailable(network);
                        connectivityManager.unregisterNetworkCallback(this);
                        reloadPage();
                    }
                });
            }
        }
    }

    private void reloadPage() {
        // 重新加载页面
        if (geckoSession != null) {
            geckoSession.reload();
        }
        if (inputPop != null){
            String ip = Util.getIPAddress(true);
            String address = "http://" + ip + ":8080";
            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(address, 500);
            inputPop.setAddress(address);
            inputPop.setQrBitmap(bitmap);
        }
    }

}

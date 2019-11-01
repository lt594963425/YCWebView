package com.ycbjie.ycwebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ycbjie.webviewlib.BridgeHandler;
import com.ycbjie.webviewlib.CallBackFunction;
import com.ycbjie.webviewlib.DefaultHandler;
import com.ycbjie.webviewlib.ImageJavascriptInterface;
import com.ycbjie.webviewlib.InterWebListener;
import com.ycbjie.webviewlib.X5WebView;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/9/17
 *     desc  : webView页面
 *     revise: 暂时先用假数据替代
 * </pre>
 */
public class NativeActivity2 extends AppCompatActivity {

    private X5WebView mWebView;
    private ProgressBar pb;
    private Button btn;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack() && event.getKeyCode() ==
                KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
            //mWebView = null;
        }
        super.onDestroy();
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.getSettings().setJavaScriptEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWebView != null) {
            mWebView.getSettings().setJavaScriptEnabled(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_view2);
        initData();
        initView();
    }


    public void initData() {

    }

    public void initView() {
        mWebView = findViewById(R.id.web_view);
        pb = findViewById(R.id.pb);
        mWebView.loadUrl("file:///android_asset/js_interaction/hello.html");
        mWebView.getX5WebChromeClient().setWebListener(interWebListener);
        mWebView.getX5WebViewClient().setWebListener(interWebListener);
        initWebViewBridge();

        findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 无参数调用
                mWebView.loadUrl("javascript:callByAndroid()");

//                mWebView.callHandler("callByAndroid", "", new CallBackFunction() {
//                    @Override
//                    public void onCallBack(String data) {
//                        Toast.makeText(NativeActivity2.this,"reponse data from js " + data,Toast.LENGTH_SHORT).show();
//                        Log.i("java调用web----", "reponse data from js " + data);
//                    }
//                });
            }
        });
        findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("javascript:callByAndroidParam(\"你个傻逼\")");
//                mWebView.callHandler("callByAndroidParam", "data from Java",
//                        new CallBackFunction() {
//                            @Override
//                            public void onCallBack(String data) {
//                                Log.i("java调用web----", "reponse data from js " + data);
//                                Toast.makeText(NativeActivity2.this,"reponse data from js " + data,Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });
        findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("javascript:callByAndroidMoreParams(\"传递多个参数\",\"杨充\",\"傻逼\")");

//                mWebView.callHandler("callByAndroidMoreParams", "传递多个参数",
//                        new CallBackFunction() {
//                            @Override
//                            public void onCallBack(String data) {
//                                Log.i("java调用web----", "reponse data from js " + data);
//                                Toast.makeText(NativeActivity2.this,"reponse data from js " + data,Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });
        findViewById(R.id.four).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("javascript:callByAndroidInteraction(\"你好js逗比\")");
//                mWebView.callHandler("callByAndroidInteraction", "你好js逗比",
//                        new CallBackFunction() {
//                            @Override
//                            public void onCallBack(String data) {
//                                Log.i("java调用web----", "reponse data from js " + data);
//                                Toast.makeText(NativeActivity2.this,"reponse data from js " + data,Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });
    }


    private InterWebListener interWebListener = new InterWebListener() {
        @Override
        public void hindProgressBar() {
            pb.setVisibility(View.GONE);
        }

        @Override
        public void showErrorView() {

        }

        @Override
        public void startProgress(int newProgress) {
            pb.setProgress(newProgress);
        }

        @Override
        public void showTitle(String title) {

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //这个是处理回调逻辑
        mWebView.getX5WebChromeClient().uploadMessageForAndroid5(data,resultCode);
    }

    @JavascriptInterface
    public void initWebViewBridge() {
        mWebView.setDefaultHandler(new DefaultHandler());
        mWebView.addJavascriptInterface(new AndroidInterface(mWebView,this), "android");
    }

    public class AndroidInterface {

        private X5WebView superWeb;
        private Context context;

        public AndroidInterface(X5WebView superWeb, Context context) {
            this.superWeb = superWeb;
            this.context = context;
        }

        private Handler deliver = new Handler(Looper.getMainLooper());

        @JavascriptInterface
        public void callAndroid(final String msg) {
            deliver.post(new Runnable() {
                @Override
                public void run() {

                    Log.i("Info", "main Thread:" + Thread.currentThread());
                    Toast.makeText(context.getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                }
            });
            Log.i("Info", "Thread:" + Thread.currentThread());

            //对外接口
        }

    }


}
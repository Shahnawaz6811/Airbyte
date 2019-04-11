package com.airbyte.admin;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

import com.airbyte.R;

public class PaymentWebView extends AppCompatActivity {
    @BindView(R.id.webview)
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);
        ButterKnife.bind(this);

        webView.loadUrl("http://www.infinitywebtechno.com/demo/android/payment/index.php");
    }
}

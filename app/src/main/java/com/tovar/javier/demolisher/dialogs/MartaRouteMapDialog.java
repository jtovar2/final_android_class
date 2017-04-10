package com.tovar.javier.demolisher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.tovar.javier.demolisher.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by javier on 4/9/17.
 */

public class MartaRouteMapDialog extends Dialog
{
    String busRoute;


    WebView webView;
    public MartaRouteMapDialog(Context ctx, String newBusRoute)
    {
        super(ctx, R.style.AppTheme);
        setContentView(R.layout.dialog_marta_route);
        ButterKnife.bind(this);
        busRoute = newBusRoute;


        webView = (WebView) findViewById(R.id.dialog_webview);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        String url = "http://itsmartaelb-727066833.us-east-1.elb.amazonaws.com/"+ busRoute + ".aspx";
        Log.d(MartaRouteMapDialog.class.toString(), url);
        webView.loadUrl(url);

        Log.d(MartaRouteMapDialog.class.toString(), webView.getUrl());

    }

    @OnClick(R.id.dialog_cancel_btn)
    public void cancelDialog()
    {
        this.dismiss();
    }

}

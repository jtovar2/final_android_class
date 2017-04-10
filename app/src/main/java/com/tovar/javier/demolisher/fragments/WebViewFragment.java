package com.tovar.javier.demolisher.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class WebViewFragment extends Fragment {


    private static final String URL = "website_url";



    private String mUrl;
    private WebView webView;



    public WebViewFragment() {
        // Required empty public constructor
    }



    public static WebViewFragment newInstance(String websiteUrl) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(URL, websiteUrl);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(URL);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        webView = new WebView(inflater.getContext());
        webView.loadUrl(mUrl);
        return webView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);




    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}

package com.airbyte.admin;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ServiceHandler {
    private static final String TAG = "ServiceHandler";

    public void makeServiceCall() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "https://secure.ccavenue.com/transaction/transaction.do";
                HttpPost post = new HttpPost(url);
                HttpResponse response = null;
                HttpEntity httpEntity = null;

                Log.d(TAG, "makeServiceCall: 27");


                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("access_code", "AVLL83GC82AI10LLIA"));
                params.add(new BasicNameValuePair("currency", "inr"));
                params.add(new BasicNameValuePair("amount", "100"));

                try {
                    Log.d(TAG, "makeServiceCall: 38");
                    post.setEntity(new UrlEncodedFormEntity(params));
                } catch (UnsupportedEncodingException e) {
                    Log.d(TAG, "makeServiceCall: 41");
                    e.printStackTrace();
                }

                DefaultHttpClient httpClient = new DefaultHttpClient();
                try {
                    Log.d(TAG, "makeServiceCall: 46");
                    response = httpClient.execute(post);
                    httpEntity = response.getEntity();
                    String strResponse = EntityUtils.toString(httpEntity);
                    Log.d(TAG, "makeServiceCall: ");

                } catch (IOException e) {
                    Log.d(TAG, "makeServiceCall: 53");
                    e.printStackTrace();
                }


            }


        });

    }

}

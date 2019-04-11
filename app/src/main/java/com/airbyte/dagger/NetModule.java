package com.airbyte.dagger;

import android.content.Context;

import com.airbyte.utils.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.ContentHandler;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {

    String baseUrl;

    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Singleton
    @Provides
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient client) {
        return new Retrofit.Builder().baseUrl(this.baseUrl)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(new Gson().newBuilder().setLenient().create()))
                            .client(client).build();
    }

    @Singleton
    @Provides
    Session providesSession(Context context){
        return  new Session(context);
    }
}

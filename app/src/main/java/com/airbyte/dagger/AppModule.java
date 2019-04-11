package com.airbyte.dagger;

import android.content.Context;

import com.airbyte.BaseView;
import com.airbyte.verification.NumberVerifyPresenter;
import com.airbyte.verification.VerifyOtpActivityPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

import javax.inject.Singleton;

@Module
public class AppModule {
    private Context context;
    private BaseView baseView;

    public AppModule(Context context,BaseView view) {
        this.baseView = view;
        this.context = context;
    }

    @Singleton
    @Provides
    Context providesContext() {
        return this.context;
    }

    @Singleton
    @Provides
    NumberVerifyPresenter providesPresenter(Retrofit retrofit) {
       return new NumberVerifyPresenter(baseView,retrofit);
    }

    @Singleton
    @Provides
    VerifyOtpActivityPresenter providesOtpVerifyPresenter(Retrofit retrofit) {
        return new VerifyOtpActivityPresenter(baseView,retrofit);
    }
}

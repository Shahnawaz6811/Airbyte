package com.airbyte.dagger;

import com.airbyte.admin.AddGroupFragment;
import com.airbyte.admin.AddUserFragment;
import com.airbyte.admin.AddWorkerFragment;
import com.airbyte.admin.ComplaintListFragment;
import com.airbyte.admin.CreateComplaintTemplateFragment;
import com.airbyte.admin.CreateSmsTemplateFragment;
import com.airbyte.admin.CustomerDialogFragment;
import com.airbyte.admin.ManageGroupFragment;
import com.airbyte.admin.PaymentListFragment;
import com.airbyte.admin.CustomerListFragment;
import com.airbyte.admin.SendMessageCustomerFragment;
import com.airbyte.admin.SendMessageGroupFragment;
import com.airbyte.customer.ContactUsFragment;
import com.airbyte.customer.complaints.ComplaintFragment;
import com.airbyte.customer.HomeActivity;
import com.airbyte.customer.payment.CheckSumActivity;
import com.airbyte.customer.payment.PaymentDetailActivity;
import com.airbyte.customer.payment.PaymentFragment;
import com.airbyte.verification.NumberVerifyActivity;
import com.airbyte.verification.OtpVerifyActivity;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {NetModule.class,AppModule.class})
public abstract class AppComponent {

    public abstract Retrofit getRetrofit();
   public abstract void inject(NumberVerifyActivity activity);
   public abstract void inject(OtpVerifyActivity activity);
   public abstract void inject(ComplaintFragment fragment);
   public abstract void inject(PaymentFragment fragment);
   public abstract void inject(ComplaintListFragment fragment);
   public abstract void inject(PaymentListFragment fragment);
   public abstract void inject(CustomerListFragment fragment);
   public abstract void inject(AddUserFragment fragment);
   public abstract void inject(AddGroupFragment fragment);
   public abstract void inject(AddWorkerFragment fragment);
   public abstract void inject(SendMessageCustomerFragment fragment);
   public abstract void inject(ManageGroupFragment fragment);
   public abstract void inject(CustomerDialogFragment fragment);
    public abstract void inject(SendMessageGroupFragment fragment);
    public abstract void inject(PaymentDetailActivity activity);
    public abstract void inject(ContactUsFragment fragment);
     public abstract void inject(HomeActivity activity);
     public abstract void inject(CheckSumActivity activity);
    public abstract void inject(com.airbyte.admin.HomeActivity activity);
  }

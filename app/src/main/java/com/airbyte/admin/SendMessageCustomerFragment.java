package com.airbyte.admin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.NetModule;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.MessageApiService;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;

public class SendMessageCustomerFragment extends BaseFragment implements SelectCustomerListAdapter.CustomerSelectListener,
        CustomerFragmentListView, SmsTemplateFetchListener,SendMessageCustomerView {

    @BindView(R.id.et_customer_id)
    EditText customerIdText;
    @BindView(R.id.button)
    Button sendMsgBtn;
    @BindView(R.id.tv_phone)
    TextView phoneTxt;
    @BindView(R.id.tv_name)
    TextView nameTxt;
    @BindView(R.id.spinner_templates)
    MaterialSpinner spinner;
    @BindView(R.id.et_templates)
    EditText templateTxt;
    @BindView(R.id.progress_bar)
    TashieLoader progressBar;
    AdminHomePresenter adminHomePresenter;
    SendMessagePresenter sendMessagePresenter;
    List<Customer> customers;
    SelectCustomerDialogFragment dialogFragment;
    @Inject
    Retrofit retrofit;
    List<String> smsTemplates = new ArrayList<>();
 

    private static final String TAG = "SendMessageCustomerFrag";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
        adminHomePresenter = new AdminHomePresenter(retrofit,this);
         adminHomePresenter.fetchCustomers();
         adminHomePresenter.fetchSmsTemplates(this);


    }

      static SendMessageCustomerFragment newInstance() {

        Bundle args = new Bundle();
        args.putString("title","Customer");
        SendMessageCustomerFragment fragment = new SendMessageCustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutRes() {
        return R.layout.layout_send_customer_notifcation;
    }

    @Override
    public void initView() {

        customerIdText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (customerIdText.getRight() - customerIdText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Log.d(TAG, "initView: ");
                    if (customers != null) {
                        dialogFragment = new SelectCustomerDialogFragment(this.customers,this::onCustomerSelected);
                        dialogFragment.show(getChildFragmentManager(),"dialog");
                    }
                    else{
                        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
                            @Override
                            public void onRetry() {
                                adminHomePresenter.fetchCustomers();
                                return;
                            }

                            @Override
                            public void onCancel() {
                                return;

                            }
                        });
                    }

                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminHomePresenter = new AdminHomePresenter(retrofit,this);
     }

    @Override
    protected void onNetworkActive() {
        adminHomePresenter.fetchCustomers();
    }


    @Override
    public void onCustomerSelected(Customer customer) {
        Log.d(TAG, "onCustomerSelected: ");
        phoneTxt.setText(customer.getCustomerPhone1());
        nameTxt.setText(customer.getCustomerName());
        customerIdText.setText(customer.getCustomerId());
    }

    @Override
    public void onUserListFetchSuccess(List<Customer> customerList) {

        if (customerList != null && !customerList.isEmpty()){
            this.customers = customerList;
    }
        Log.d(TAG, "onUserListFetchSuccess: "+ this.customers);
    }


    @Override
    public void onUserListFetchFailed() {
        SmsTemplateFetchListener listener = this;

        showConnectionErrorPopup(getActivity(),new NetworkErrorDialogCallback(){

            @Override
            public void onRetry() {
                adminHomePresenter.fetchSmsTemplates(listener);
                adminHomePresenter.fetchCustomers();
             }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Dismiss CLicked");
            }
        });
    }

    @Override
    public void onSmsTemplateFetchSuccess(List<String> smsTemplatList) {

        Log.d(TAG, "onSmsTemplateFetchSuccess: ");

         smsTemplates.add("Select template");
         smsTemplates.addAll(smsTemplatList);
         spinner.setItems(this.smsTemplates);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                templateTxt.setText(smsTemplates.get(spinner.getSelectedIndex()));
                smsTemplates.remove("Select template");


            }
        });



    }

    @Override
    public void onSmsTemplateFetchError() {
        Log.d(TAG, "onSmsTemplateFetchError: ");
    }

    @OnClick(R.id.button)
    public void onClick(View view) {

        if (customerIdText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select customer", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (smsTemplates.get(spinner.getSelectedIndex()).equals("Select template")) {
                Toast.makeText(getActivity(), "Please select any template", Toast.LENGTH_SHORT).show();
                return;

            }
        }catch (Exception e) {
            Log.d(TAG, "onClick: Caught");
        }




        if (templateTxt.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Template field is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(getActivity())){
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }


        sendMsgBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "onClick: SendMessage");
        sendMessagePresenter = new SendMessagePresenter(this,retrofit);
        sendMessagePresenter.sendMessageToCustomer(phoneTxt.getText().toString(),templateTxt.getText().toString());
        
     }

    @Override
    public void onSendMessageSuccess() {
        templateTxt.setText("");
        sendMsgBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Message Sent Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageError() {
        sendMsgBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Unexpected error occured,Please try in a while", Toast.LENGTH_SHORT).show();
    }
}

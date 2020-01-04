package com.airbyte.admin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;

import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.customer.complaints.ComplaintFragment;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends BaseFragment implements AddUserFragmentView{

    @BindView(R.id.et_customer_id)
    EditText customerIdText;
    @BindView(R.id.et_customer_name)
    EditText customerNameText;
    @BindView(R.id.et_customer_phone1)
    EditText customerPhone1Text;
    @BindView(R.id.et_customer_phone2)
    EditText customerPhone2Text;
    @BindView(R.id.et_customer_address)
    EditText customerAddressText;
    @BindView(R.id.btn_add_customer)
    Button addCustomerBtn;
    @BindView(R.id.progress_bar)
    TashieLoader progress;
    AdminServicePresenter presenter;
    @Inject
    Retrofit retrofit;


    private static final String TAG = "AddUserFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_add_user;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onNetworkActive() {

    }

    @Override
    public void onCustomerAddSuccess() {
        addCustomerBtn.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
         Toast.makeText(getActivity(), "Customer Added successfully", Toast.LENGTH_SHORT).show();
         getActivity().startActivity(getActivity().getIntent());
         getActivity().finish();

//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new ComplaintListFragment(), "findThisFragment")
//                .addToBackStack(null)
//                .commit();
//
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

    }

    @Override
    public void onCustomerAddFailed(String message) {
        Log.d(TAG, "onCustomerAddFailed: ");
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
         addCustomerBtn.setVisibility(View.VISIBLE);
         progress.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_add_customer)
    public void onClick(View view) {

         validateFields();
    }

    private void validateFields() {

        if (customerIdText.getText().toString().isEmpty()) {
            customerIdText.setError("Customer Id can't be empty");
            return;
        }


        if (customerNameText.getText().toString().isEmpty()) {
            customerNameText.setError("Customer Name can't be empty");
            return;
        }


        if (customerAddressText.getText().toString().isEmpty()) {
            customerAddressText.setError("Address field can't be empty");
            return;
        }





        if (!customerPhone1Text.getText().toString().isEmpty() && customerPhone1Text.getText().toString().length() < 10 ) {
            customerPhone1Text.setError("Phone number should be 10 digit long");
            return;
        }


        if (!customerPhone2Text.getText().toString().isEmpty() && customerPhone2Text.getText().toString().length() < 10 ) {
            customerPhone2Text.setError("Phone number should be 10 digit long");
            return;
        }

        if (customerPhone1Text.getText().toString().isEmpty() && customerPhone2Text.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"At least one number is required",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }


        presenter = new AdminServicePresenter(this,retrofit);
        progress.setVisibility(View.VISIBLE);
        addCustomerBtn.setVisibility(View.GONE);
        presenter.addNewCustomer(customerIdText.getText().toString(),customerNameText.getText().toString(),customerPhone1Text.getText().toString(),customerPhone2Text.getText().toString(),customerAddressText.getText().toString());


    }
}

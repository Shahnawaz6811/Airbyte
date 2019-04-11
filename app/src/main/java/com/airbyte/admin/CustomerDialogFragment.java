package com.airbyte.admin;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.customer.complaints.Complaint;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.DaggerAppComponent;
import com.airbyte.dagger.NetModule;
import com.airbyte.utils.NetworkUtil;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;


public class CustomerDialogFragment extends DialogFragment implements CustomerUpdateView {

    private static final String TAG = "CustomerDialogFragment";
    @BindView(R.id.et_customer_id)
    TextView customerIdText;
    @BindView(R.id.et_customer_name)
    TextView customerNameText;
    @BindView(R.id.et_customer_phone1)
    TextView customerPhone1Text;
    @BindView(R.id.et_customer_phone2)
    TextView customerPhone2Text;
    @BindView(R.id.et_customer_address)
    TextView customerAddressText;
    @BindView(R.id.progress_bar)
    TashieLoader tashieLoader;
    @BindView(R.id.btn_update_customer)
    Button customerBtnUpdateDetail;
    Customer customer;
    AdminHomePresenter presenter;
    @BindView(R.id.ib_dismiss)
    ImageButton btnDismissDialog;
    @Inject
    Retrofit retrofit;
    CustomerUpdateListener listener;

    interface CustomerUpdateListener {
        void customerUpdateSuccess();
    }

    void setCustomerUpdateListener(CustomerUpdateListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_dialog, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DaggerAppComponent.builder().netModule(new NetModule(AdminApiService.baseUrl)).appModule(new AppModule(getActivity(),this)).build().inject(this);

        presenter = new AdminHomePresenter(retrofit,this);

       Customer customer =  getArguments().getParcelable("customer");

        customerIdText.setText(customer.getCustomerId());
        customerNameText.setText(customer.getCustomerName());
        customerPhone1Text.setText(customer.getCustomerPhone1());
        customerPhone2Text.setText(customer.getCustomerPhone2());
        customerAddressText.setText(customer.getCustomerAddress());


        customerBtnUpdateDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(getActivity(), "It seems connection is down", Toast.LENGTH_SHORT).show();
                    return;
                }

                tashieLoader.setVisibility(View.VISIBLE);
                customerBtnUpdateDetail.setVisibility(View.GONE);
                presenter.updateUserDetail(customer.getCtId(),customerNameText.getText().toString(), customerAddressText.getText().toString(),customerPhone1Text.getText().toString(),customerPhone2Text.getText().toString());

            }
        });

    }


    @Override
    public void onCustomerUpdateSuccess(String message) {
        Toast.makeText(getActivity(), "Customer update successfully", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCustomerUpdateSuccess: ");
        listener.customerUpdateSuccess();
        dismiss();
     }

    @Override
    public void onCustomerUpdateFailed() {
        Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT).show();
         Log.d(TAG, "onCustomerUpdateFailed: ");
    }

    @OnClick(R.id.ib_dismiss)
    public void onClick(View view){
        dismiss();

    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.customer_dialog_height);
        getDialog().getWindow().setLayout(width,height);
    }

    @Override
    public void onStart() {
        super.onStart();

        final View decorView = getDialog()
                .getWindow()
                .getDecorView();

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
        scaleDown.setDuration(500);
        scaleDown.start();


        btnDismissDialog.setOnClickListener(v -> {
            final View decorView1 = getDialog()
                    .getWindow()
                    .getDecorView();

            ObjectAnimator scaleDown1 = ObjectAnimator.ofPropertyValuesHolder(decorView1,
                    PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f),
                    PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f));
            scaleDown1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dismiss();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            scaleDown1.setDuration(300);
            scaleDown1.start();
        });
    }
}

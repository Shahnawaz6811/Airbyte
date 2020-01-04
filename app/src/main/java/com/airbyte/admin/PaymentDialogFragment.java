package com.airbyte.admin;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbyte.R;
import com.airbyte.customer.complaints.Complaint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PaymentDialogFragment extends DialogFragment {

    @BindView(R.id.tv_amount)
    TextView amountTxt;
    @BindView(R.id.tv_date)
    TextView paymentDate;
    @BindView(R.id.tv_customer_id)
    TextView customerIdTxt;
    @BindView(R.id.tv_customer_phone)
    TextView customerPhoneTxt;
    @BindView(R.id.txn_id)
    TextView txnIdTxt;

    @BindView(R.id.ib_dismiss)
    ImageButton dismissBtn;

    Payment payment;


    private static final String TAG = "PaymentDialogFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_dialog, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       Payment payment =  getArguments().getParcelable("payment");
        Log.d(TAG, "onViewCreated: "+ payment.getCustomerId());

        amountTxt.setText(payment.getPaymentAmount());
       customerIdTxt.setText(payment.getCustomerId());
       paymentDate.setText(payment.getPaymentDate());
       txnIdTxt.setText(payment.getTxnId());
       customerPhoneTxt.setText(payment.getPhone1());


    }

    @OnClick(R.id.ib_dismiss)
    public void onClick(View view){
        dismiss();

    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
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


        dismissBtn.setOnClickListener(v -> {
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

package com.airbyte.admin;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbyte.R;
import com.airbyte.customer.complaints.Complaint;
import com.airbyte.dagger.AppModule;
 import com.airbyte.dagger.NetModule;


public class ComplaintDialogFragment extends DialogFragment {

    @BindView(R.id.tv_complaint)
    TextView complaintTxt;
    @BindView(R.id.tv_complaint_id)
    TextView complaintIdTxt;
    @BindView(R.id.tv_customer_id)
    TextView customerIdTxt;
    @BindView(R.id.tv_customer_phone)
    TextView customerPhoneTxt;
    @BindView(R.id.tv_complaint_time)
    TextView complaintTimeTxt;

    @BindView(R.id.ib_dismiss)
    ImageButton dismissBtn;

    Complaint complaint;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complaint_dialog, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       Complaint complaint =  getArguments().getParcelable("complaint");

       complaintIdTxt.setText(complaint.getComplaintId());
       complaintTimeTxt.setText(complaint.getComplaintTime());
       complaintTxt.setText(complaint.getComplaintInfo());
       customerPhoneTxt.setText(complaint.getCustomerPhone());
       customerIdTxt.setText(complaint.getCustomerId());


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

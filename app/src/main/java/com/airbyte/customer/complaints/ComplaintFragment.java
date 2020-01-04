package com.airbyte.customer.complaints;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.customer.HomeActivity;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.MessageApiService;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.utils.Session;
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

public class ComplaintFragment extends BaseFragment implements ComplaintFragmentView {
    public static final String CLIENT = "8006138003";
    private static final String TAG = "ComplaintFragment";
    String complaintBody;
    HomeActivity homeActivity;
    List<String> complaintList;
    @BindView(R.id.et_other_complaint)
    EditText otherComplaintTxt;
    String phone;
    ComplaintFragmentPresenter presenter;
    @BindView(R.id.progress)
    TashieLoader progressBar;
    @BindView(R.id.other_complaint_layout)
    RelativeLayout otherComplaintLayout;
    @Inject
    Retrofit retrofit;
    @Inject
    Session session;
    @BindView(R.id.complaint_spinner)
    MaterialSpinner complaintSpinner;
    @BindView(R.id.btn_submit)
    Button submitComplaint;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = ((HomeActivity) context);
        Log.d(TAG, "onAttach: ");
        getDaggerBuilder(MessageApiService.api91BaseUrl,this).build().inject(this);
         presenter = new ComplaintFragmentPresenter(this,retrofit);
    }


    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view =  super.onCreateView(inflater,container,savedInstanceState);
         ButterKnife.bind(this,view);
         return view;
    }



    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_complaint;
    }

    @Override
    public void initView() {

        setupSpinner();

    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");


    }

    private void setupSpinner() {

        complaintList = new ArrayList<String>();
        complaintList.add("Internet not working ");
        complaintList.add("Internet is slow");
         complaintList.add("Wire is cut");
        complaintList.add("Router is reset");
        complaintList.add("Wifi password forgot");
        complaintList.add("Other");

        complaintSpinner.setItems(complaintList);
        otherComplaintLayout.setVisibility(View.GONE);
        complaintSpinner.setOnItemSelectedListener((view, position, id, item) -> {

            if ((complaintList.get(position)).equals("Other")) {
                Log.d(TAG, "onItemSelected: ");
                otherComplaintLayout.setVisibility(View.VISIBLE);
                return;
            }


            otherComplaintLayout.setVisibility(View.GONE);
        });
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity.getWindow().setSoftInputMode(32);
        presenter = new ComplaintFragmentPresenter(this, retrofit);
        presenter.getComplaints();

        if (session.getAdminPhone() == null) {
//             presenter.getAdminPhone();
        }
        if ( session.getWorkerPhone() == null) {
//              presenter.getWorkerPhone();
        }
    }

    public void onResume() {
        super.onResume();
        homeActivity.getWindow().setSoftInputMode(32);



        if (complaintList.get(complaintSpinner.getSelectedIndex()).equals("Other")){
            otherComplaintLayout.setVisibility(View.VISIBLE);
        }
        else{
            otherComplaintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNetworkActive() {

    }

    @OnClick(R.id.btn_submit)
    public void onClick(View view) {

        Log.d(TAG, "onClick: 175");
        String selectedComplaint = complaintList.get(complaintSpinner.getSelectedIndex());
        String otherComplaint = otherComplaintTxt.getText().toString();
        String mainComplaint;
        if (selectedComplaint.equals("Other")) {
            mainComplaint = otherComplaint;

            if (mainComplaint.length() < 10) {
                otherComplaintTxt.setError("Seems Complaint is too short");
                return;
            }
        }
        else{
            mainComplaint = selectedComplaint;
        }


        if (!NetworkUtil.isNetworkAvailable(homeActivity)) {   //Network is not available, send complaint via sms
            sendComplaintViaSms(mainComplaint);
        }
        //Network available
        else{
            Log.d(TAG, "onClick: ");
            sendComplaintViaInternet(mainComplaint);
        }

         }



    private void sendComplaintViaInternet(String complaint) {
        Log.d(TAG, "sendComplaint: ");
        complaintBody = complaint;
        submitComplaint.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        presenter.registerComplaint(session.getCustomerId(),complaintBody);

    }



    private void sendComplaintViaSms(String complaintBody) {
        AppUtils appUtils = new AppUtils();
        appUtils.showNetworkErrorDialog(homeActivity);
        appUtils.getAlertDialog().setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String smsBody;
                Log.d(TAG, "onDismiss: "+ session.getAdminPhone());
                Intent smsIntent = new Intent("android.intent.action.VIEW", Uri.parse("sms:"+session.getAdminPhone()));
                if (complaintList.get(complaintSpinner.getSelectedIndex()).equals("Other")) {
                    smsBody = otherComplaintTxt.getText().toString();
                } else {
                    smsBody = complaintList.get(complaintSpinner.getSelectedIndex());
                }


                smsIntent.putExtra("sms_body", "I have a complaint for  "+ smsBody);
                startActivity(smsIntent);
            }
        });
    }

    public void onComplaintTemplatesSuccess(List<String> complaints) {

          complaintList.clear();
          complaintList = complaints;
          complaintSpinner.setItems(complaintList);

        if (otherComplaintLayout.getVisibility() == View.VISIBLE){
            complaintSpinner.setText("Other");
        }
     }

     

    public void onComplaintRegisterSuccess(String s) {
        Toast.makeText(homeActivity, "Complaint Registered", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onComplaintRegisterSuccess: ");
        otherComplaintTxt.setText("");
        progressBar.setVisibility(View.GONE);
        submitComplaint.setVisibility(View.VISIBLE);


        presenter.sendMessageToUser(session.getCustomerPhone(),complaintBody);
        presenter.sendMessageToWorker( session.getWorkerPhone(),  complaintBody,  session.getCustomerPhone());  //works fine
        this.presenter.sendMessageToAdmin( session.getAdminPhone(),  complaintBody,  session.getCustomerPhone(),  session.getCustomerId());

     }

    public void onComplaintRegisterFailed() {
        progressBar.setVisibility(View.GONE);
        submitComplaint.setVisibility(View.VISIBLE);
        Log.d(TAG, "onComplaintSentFailed: ");
        Toast.makeText(homeActivity, "Unexpected error while registering complaint.", Toast.LENGTH_SHORT).show();
    }



    public void onAdminPhoneFetchSuccess(String phone) {
         session.setAdminPhone(phone);
         Log.d(TAG, "onAdminPhoneFetchSuccess: ");
    }

    @Override
    public void handleComplaintTemplatesError() {
        Log.d(TAG, "handleComplaintTemplatesError: ");
    }



    public void onAdminPhoneFetchFailed() {
        Log.d(TAG, "onAdminPhoneFetchFailed: ");
    }

    public void onWorkerPhoneFetchSuccess(String phone) {
         Log.d(TAG, "onWorkerPhoneFetchSuccess: ");
         session.setWorkerPhone(phone);
    }

    public void onWorkerPhoneFetchFailed() {
        Log.d(TAG, "onWorkerPhoneFetchFailed: ");
    }

    public void onSendMessageSuccess(String message) {
        Log.d(TAG, "onSendMessageSuccess: ");
    }

    public void onSendMessageFailed() {
        Log.d(TAG, "onSendMessageFailed: ");
    }
}

package com.airbyte.customer.payment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.admin.PaymentWebView;
import com.airbyte.customer.CustomerApiService;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.utils.Session;
import com.airbyte.verification.BaseFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;

public class PaymentFragment extends BaseFragment implements PaymentFragmentView{
    private static final String TAG = "PaymentFragment";
    @BindView(R.id.btn_send_pay_rqst)
    Button payRqstBtn;
    @BindView(R.id.et_outstanding_amount)
    EditText amountTxt;
    @BindView(R.id.btn_pay)
    Button payBtn;
    @BindView(R.id.txn_id)
    TextView txnTxt;
    @BindView(R.id.tv_last_bill_date)
    TextView txnDate;
    @BindView(R.id.tv_last_bill_amount)
    TextView lastPaymentAmount;
    @BindView(R.id.tv_payee_name)
    EditText payeeNameText;
    @BindView(R.id.progress_payment)
    TashieLoader progressBarPayment;
    @BindView(R.id.last_payment_layout)
    RelativeLayout lastPaymentLayout;
    @BindView(R.id.bill_date_status)
    TextView billDateStatus;
    @Inject
    Session session;
    @Inject
    Retrofit retrofit;
    @BindView(R.id.tv_c_id)
    TextView cIdTxt;
    @BindView(R.id.tv_billing_date)
    TextView billDate;
    @BindView(R.id.tv_due_date)
    TextView dueDateTxt;
    @BindView(R.id.progress_send_pay_request)
    TashieLoader progressPayRequest;
    private PaymentFragmentPresenter presenter;
    Date billingDate;
    Date dueDate;
    @BindView(R.id.txn_status_txt)
    TextView txnStatusTxt;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getDaggerBuilder(CustomerApiService.BASE_URL,this).build().inject(this);
        presenter = new PaymentFragmentPresenter(retrofit,this);
    }


    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getLastPaymentDetail(session.getCustomerId());
        progressBarPayment.setVisibility(View.GONE);
        payBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        payeeNameText.setText(session.getCustomerName());
        cIdTxt.setText(session.getCustomerId());

        payBtn.setAlpha(0.5f);
        payBtn.setClickable(false);


        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        billingDate = new Date();
        billingDate.setDate(01);

        String format1 = format.format(billingDate);
        billDate.setText(format1);

        dueDate = new Date();
        dueDate.setDate(06);
        String format2 = format.format(dueDate);
        dueDateTxt.setText(format2);

        amountTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                 if (s.toString().equals("")){
                     payBtn.setClickable(false);
                     payBtn.setAlpha(0.5f);
                     billDateStatus.setVisibility(View.GONE);

                     return;
                 }

                try {

                    if (Integer.parseInt(s.toString()) < 100) {
                        billDateStatus.setVisibility(View.VISIBLE);
                        payBtn.setClickable(false);
                        payBtn.setAlpha(0.5f);
                        billDateStatus.setTextColor(Color.parseColor("#008000"));
                        billDateStatus.setText("*Amount atleast be 100");
                    }
                    else{
                        payBtn.setClickable(true);
                        payBtn.setAlpha(1);
                        billDateStatus.setVisibility(View.GONE);
                    }
                }catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Please ensure if amount is valid", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         presenter.getLastPaymentDetail(session.getCustomerId());

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_payment;
    }

    @Override
    public void initView() {
        lastPaymentLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onNetworkActive() {

    }

    @OnClick({R.id.btn_pay,R.id.btn_send_pay_rqst})
    public void onClick(View view){


        switch (view.getId()){


            case R.id.btn_pay:
                Log.d(TAG, "onClick: 95");

                makePayment();
                break;

            case R.id.btn_send_pay_rqst:
                Log.d(TAG, "onClick: 101");
                sendPayRequest();
                break;
        }

    }



    @Override
    public void onSendMessageFailed() {
        Toast.makeText(getActivity(), "Error occured while sending request", Toast.LENGTH_SHORT).show();
        progressPayRequest.setVisibility(View.GONE);
        payRqstBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSendMessageSuccess(String str) {
        Toast.makeText(getActivity(), "Payment request Sent", Toast.LENGTH_SHORT).show();
        progressPayRequest.setVisibility(View.GONE);
        payRqstBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPaymentSuccess(Payment payment) {
        if (payment != null) {

            payeeNameText.setText(session.getCustomerName());
            txnDate.setText(payment.getPaymentDate());
            txnTxt.setText(payment.getTxnId());
            lastPaymentAmount.setText(payment.getPaymentAmount());
        }

        Toast.makeText(getActivity(), "Payment Successful", Toast.LENGTH_SHORT).show();
        progressBarPayment.setVisibility(View.GONE);
        payBtn.setVisibility(View.VISIBLE);
        amountTxt.setText("");
        payment.setPayeeName(session.getCustomerName());
        Intent intent = new Intent(getActivity(), PaymentDetailActivity.class);
        intent.putExtra("payment_response", payment);
        startActivity(intent);

    }

    @Override
    public void onPaymentFailed() {
        Toast.makeText(getActivity(), "Payment Failed", Toast.LENGTH_SHORT).show();
        payBtn.setVisibility(View.VISIBLE);
        progressBarPayment.setVisibility(View.GONE);
        Log.d(TAG, "onPaymentFailed: ");
    }

    @Override
    public void onLastPaymentInfoFetchSuccess(Payment payment) {
        if (payment != null) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date lastBilldate;
            String[] strings = payment.getPaymentDate().split(" ");
            String dateStr = strings[0];
             try {
                lastBilldate =  format.parse(dateStr);
                  String format1 = format.format(lastBilldate);

                 Log.d(TAG, "Biiling date: "+billingDate.toString());
                 Log.d(TAG, "last bill date: "+lastBilldate.toString());

                 Date date = new Date();
                 Log.d(TAG, "onLastPaymentInfoFetch Current date :"+ date);

                 Log.d(TAG, "onLastPaymentInfoFetchSuccess: "+date.after(dueDate));
                  if (!lastBilldate.after(billingDate) && date.after(dueDate)) {
                      Log.d(TAG, "Payment done");
                      billDateStatus.setVisibility(View.VISIBLE);
                      billDateStatus.setTextColor(Color.parseColor("#ff0000"));
                      billDateStatus.setText("*Due date is over");

                  }
                  else{
                      Log.d(TAG, "onLastPaymentInfoFetchSuccess: Payment pending");

                  }





            } catch (ParseException e) {
                e.printStackTrace();
            }

             lastPaymentLayout.setVisibility(View.VISIBLE);
            txnDate.setText(payment.getPaymentDate());
            txnTxt.setText(payment.getOrderId());
            txnStatusTxt.setText(payment.getTxnStatus());

            session.saveMerchantId(payment.getMerchantId());
            lastPaymentAmount.setText(payment.getPaymentAmount());

        }
        else{
            lastPaymentLayout.setVisibility(View.GONE);

        }
    }

    @Override
    public void onLastPaymentInfoFetchFailed() {
        Log.d(TAG, "onLastPaymentInfoFetchFailed: ");
        lastPaymentLayout.setVisibility(View.GONE);
    }

    class MyDismissListener implements DialogInterface.OnDismissListener {
        MyDismissListener() {

        }

        public void onDismiss(DialogInterface dialog) {

            sendPayRequestViaSms();
        }
    }

    private void sendPayRequestViaSms() {

        Log.d(TAG, "onDismiss: "+ session.getAdminPhone());
        String smsBody = "You have payment request from user "+ session.getCustomerId() +"  " + session.getCustomerPhone();
        Intent smsIntent = new Intent("android.intent.action.VIEW", Uri.parse("sms:"+session.getAdminPhone()));
        smsIntent.putExtra("sms_body", smsBody);
        startActivity(smsIntent);
    }


    private void sendPayRequest() {

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {   //Network is not available, send complaint via sms
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            appUtils.getAlertDialog().setOnDismissListener(new MyDismissListener());
            return;
        }


        progressPayRequest.setVisibility(View.VISIBLE);
        payRqstBtn.setVisibility(View.GONE);
        String smsBody = "You have payment request from user "+ session.getCustomerId() +"  " + session.getCustomerPhone();
        presenter.sendPayRequest(session.getAdminPhone(), smsBody );

    }

    private void makePayment() {
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }


        if (amountTxt.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }




//        5166400046018785

        payBtn.setVisibility(View.GONE);
        progressBarPayment.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(),CheckSumActivity.class);
        Bundle extras = new Bundle();
        String orderId = generateRandomChars(session.getCustomerId()+"123456790",10);
        Log.d(TAG, "makePayment: "+ orderId);
        extras.putString("orderid",orderId);
        extras.putString("custid",session.getCustomerId());
        extras.putString("amount",amountTxt.getText().toString());
        intent.putExtra("bundle",extras);
        Log.d(TAG, "makePayment: order id"+ extras.getString("orderid"));
        startActivity(intent);

//        String txnId = (
//                "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890", 13);
//        presenter.insertPaymentInfo(session.getCustomerPhone(),session.getCustomerId(),amountTxt.getText().toString(),txnId);

//        presenter.makePayment(amountTxt.getText().toString());

    }

    public  String generateRandomChars(String candidateChars, int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }


}

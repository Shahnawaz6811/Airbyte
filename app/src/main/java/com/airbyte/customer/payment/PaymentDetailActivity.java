package com.airbyte.customer.payment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbyte.BaseView;
import com.airbyte.R;
import com.airbyte.BaseActivity;
import com.airbyte.customer.CustomerApiService;
import com.airbyte.customer.HomeActivity;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.NetModule;
import com.airbyte.utils.Session;

import javax.inject.Inject;

public class PaymentDetailActivity extends BaseActivity implements BaseView {
    private static final String TAG = "PaymentDetailActivity";

    @BindView(R.id.tv_payee)
    TextView payeeNameTxt;
    @BindView(R.id.tv_date)
    TextView txnDate;
    @BindView(R.id.txn_id)
    TextView txnIdTxt;
    @BindView(R.id.tv_amount)
    TextView paymentAmountTxt;
    @Inject
    Session session;
    @BindView(R.id.ib_clear)
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDaggerBuilder(CustomerApiService.BASE_URL,this).build().inject(this);
        super.onCreate(savedInstanceState);

    }

   @OnClick(R.id.ib_clear)
   public void onClick(View view){
       Intent intent = new Intent(this, HomeActivity.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(intent);
            finish();

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
         Log.d(TAG, "initView: ");
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("detail");
        Log.d(TAG, "initView: "+bundle);
        payeeNameTxt.setText(session.getCustomerPhone());
        txnDate.setText(bundle.getString("TXNDATE"));
        paymentAmountTxt.setText(bundle.getString("TXNAMOUNT"));
        txnIdTxt.setText(bundle.getString("ORDERID"));


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

     }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_payment_detail;
    }
}

package com.airbyte.customer.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
 import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbyte.BaseActivity;
import com.airbyte.customer.CustomerApiService;
import com.airbyte.customer.HomeActivity;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.DaggerAppComponent;
import com.airbyte.dagger.NetModule;
import com.airbyte.utils.Session;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Retrofit;


public class CheckSumActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback, PaymentFragmentPresenter.PaymentInsertCallback {
    private static final String TAG = "CheckSumActivity";
    String custid="", orderId="", mid="";
    String amount = "";
    PaymentFragmentPresenter presenter;
    @Inject
    Retrofit retrofit;
    @Inject
    Session session;
    SendUserDetailTOServerTask dl;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //initOrderId();

        DaggerAppComponent.builder().netModule(new NetModule(CustomerApiService.BASE_URL)).appModule(new AppModule(this,this)).build().inject(this);
        presenter = new PaymentFragmentPresenter(retrofit,this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = getIntent();
        Bundle extras = intent.getBundleExtra("bundle");

        orderId = extras.getString("orderid");
         custid = extras.getString("custid");
         mid = session.getMerchantId();
        Log.d(TAG, "onCreate: "+mid);
        Log.d(TAG, "onCreate: "+extras.getString("amount"));
        Log.d(TAG, "onCreate: ");
        amount =extras.getString("amount");

//        mid = "VRXZst06214726001721"; /// your marchant key

          dl = new SendUserDetailTOServerTask();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



    }


    @Override
    public void onPaymentInsertSuccess(Payment payment) {
        Log.d(TAG, "onPaymentInsertSuccess: " +bundle);
        Intent intent = new Intent(this,PaymentDetailActivity.class);
        intent.putExtra("detail",bundle);
        startActivity(intent);
    }

    @Override
    public void onPaymentInsertError() {
        Log.d(TAG, "onPaymentInsertError: ");
    }

    @Override
    public void onOrdeIdFetchSuccess(String orderId) {
         Log.d(TAG, "onOrdeIdFetchSuccess: "+orderId);

    }

    @Override
    public void onOrderIdFetchError() {
        Log.d(TAG, "onOrderIdFetchError: ");
      }

    public class SendUserDetailTOServerTask extends AsyncTask<ArrayList<String>, Void, String> {
        private static final String TAG = "SendUserDetailTOServerT";
        private ProgressDialog dialog = new ProgressDialog(CheckSumActivity.this);
         //private String orderId , mid, custid, amt;
        String url = CustomerApiService.GENRATE_CHECKSUM;
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JsonParser jsonParser = new JsonParser(getApplicationContext());
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+amount+"&WEBSITE=DEFAULT"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            Log.d(TAG, "doInBackground: "+param);
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.d("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getProductionService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amount);
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(order,null);
            // start payment service call here
            Service.startPaymentTransaction(CheckSumActivity.this, true, true,
                    CheckSumActivity.this  );
        }
    }


    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.d(TAG, "onTransactionResponse: `"+ bundle);
        this.bundle = new Bundle();
        this.bundle = bundle;
        if (bundle.getString("STATUS").equals("TXN_SUCCESS")) {

            Log.d(TAG, "onTransactionResponse: "+session.getCustomerPhone());
            Log.d(TAG, "onTransactionResponse: Success");
            presenter.insertPaymentInfo(session.getCustomerPhone(),session.getCustomerId(),bundle.getString("TXNAMOUNT"),bundle.getString("TXNID"),bundle.getString("TXNDATE"),bundle.getString("ORDERID"),bundle.getString("STATUS"));

        }
        else{
            Toast.makeText(this, "Unexpected error occured while making payment", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }
    @Override
    public void networkNotAvailable() {

        Toast.makeText(this, "Network error occured", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, "Auth error occured", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.d("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, "Error loading page, Cross check your connection", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Log.d("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
        Log.d("checksum ", " cancel call back respon  " );
        Toast.makeText(this, "Error loading page, Cross check your connection", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, "Transaction is cancelled", Toast.LENGTH_SHORT).show();
        Log.d("checksum ", "  transaction cancel " );
        Toast.makeText(this, "Error loading page, Cross check your connection", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

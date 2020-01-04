package com.airbyte.customer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.BaseView;
import com.airbyte.R;
import com.airbyte.admin.SendMessageCustomerView;
import com.airbyte.admin.SendMessageView;
import com.airbyte.customer.complaints.ComplaintFragmentPresenter;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.MessageApiService;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.utils.Session;
import com.airbyte.verification.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Retrofit;

public class ContactUsFragment extends BaseFragment implements SendMessageCustomerView, BaseView {
    private static final String TAG = "ContactUsFragment";
    @BindView(R.id.button)
    Button addTemplateBtn;
    @BindView(R.id.progress_bar)
    TashieLoader progress;
    @BindView(R.id.et_templates)
    EditText templateTxt;
    @Inject
    Retrofit retrofit;
    @Inject
    Session session;
    ContactUsPresenter presenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         Log.d(TAG, "onAttach: ");
         getDaggerBuilder(MessageApiService.api91BaseUrl,this).build().inject(this);
     }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_fragment_contact_us;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onNetworkActive() {

    }

    @OnClick(R.id.button)
    public void onClick(View view){

        String template = templateTxt.getText().toString();
        if (template.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your query", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(getActivity())){
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            sendComplaintViaSms();
            return;
        }

        addTemplateBtn.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        Log.d(TAG, "onClick: "+session.getCustomerPhone() + session.getAdminPhone() + session.getWorkerPhone());
         presenter = new ContactUsPresenter(this,retrofit);
        presenter.sendMessageToUser(session.getCustomerPhone(),templateTxt.getText().toString());
        presenter.sendMessageToWorker(session.getWorkerPhone(),  templateTxt.getText().toString(),  session.getCustomerPhone(),session.getCustomerId());  //works fine
         presenter.sendMessageToAdmin( session.getAdminPhone(),  templateTxt.getText().toString(),  session.getCustomerPhone(),  session.getCustomerId());
//
    }

    private void sendComplaintViaSms() {
        AppUtils appUtils = new AppUtils();
         appUtils.getAlertDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String smsBody;
                Log.d(TAG, "onDismiss: "+ session.getAdminPhone());
                Intent smsIntent = new Intent("android.intent.action.VIEW", Uri.parse("sms:"+session.getAdminPhone()));


                smsIntent.putExtra("sms_body", templateTxt.getText().toString());
                startActivity(smsIntent);
            }
        });
    }


    @Override
    public void onSendMessageSuccess() {
        Toast.makeText(getActivity(), "Message Successfully sent", Toast.LENGTH_SHORT).show();
        progress.setVisibility(View.GONE);
        addTemplateBtn.setVisibility(View.VISIBLE);
        presenter.insertContactQuery(session.getCustomerId(),templateTxt.getText().toString());
    }
    @Override
    public void onSendMessageError() {
        Toast.makeText(getActivity(), "Unexpected error occured, Please check your connection", Toast.LENGTH_SHORT).show();
        progress.setVisibility(View.GONE);
        addTemplateBtn.setVisibility(View.VISIBLE);
    }
}

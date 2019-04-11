package com.airbyte.admin;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Retrofit;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageGroupFragment extends BaseFragment implements SendMessageView,SmsTemplateFetchListener{

    private static final String TAG = "SendNotificationFragmen";
    @BindView(R.id.spinner_groups)
    MaterialSpinner spinnerGroups;
    @BindView(R.id.et_message)
    EditText messageTxt;
    @BindView(R.id.btn_send_message)
    Button sendMsgBtn;
    @BindView(R.id.spinner_templates)
    MaterialSpinner spinner;
    @BindView(R.id.progress_bar)
    TashieLoader progressBar;
    private SendMessagePresenter presenter;
    private List<String> groupNames;
    private List<Group> mGroups = new ArrayList<>();
    @Inject
    Retrofit retrofit;
    private List<String> mPhoneList;
    @BindView(R.id.pb)
    ProgressBar customerPhonesFetchrogress;
    @BindView(R.id.tv_progress_txt)
    TextView progressTxt;
    private String mSelectedGroupId;

    List<String> smsTemplates = new ArrayList<>();

    public static SendMessageGroupFragment newInstance() {

        Bundle args = new Bundle();
        args.putString("title","Group");

        SendMessageGroupFragment fragment = new SendMessageGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater,container,savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: "+ context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);

        presenter = new SendMessagePresenter(this,retrofit);
        presenter.fetchGroups();
        presenter.fetchSmsTemplates(this);


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

    }



    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(TAG, "onInflate: ");
    }



    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_send_message_group;
    }

    @Override
    public void initView() {

    }


    @Override
    protected void onNetworkActive() {
        Log.d(TAG, "onNetworkActive: ");
        if (mGroups.isEmpty()) {

            Log.d(TAG, "onNetworkActive: Group not yet fetched");
            presenter.fetchGroups();
        }
    }

    @Override
    public void onGroupFetchSuccess(List<Group> groups) {
        Log.d(TAG, "onGroupFetchSuccess: "+ groups);

        /**
         * get the group names to show in list
         */
        groupNames = new ArrayList<>();
        groupNames.add("Select Group");

        if (groups != null && !groups.isEmpty()){
            this.mGroups = new ArrayList<>();
            mGroups.addAll(groups);

            for (Group group : mGroups) {
                groupNames.add(group.getGroupName());
            }

            spinnerGroups.setItems(groupNames);
        }


        spinnerGroups.setOnItemSelectedListener((view, position, id, item) -> {

            String selectedName = groupNames.get(spinnerGroups.getSelectedIndex());
            Log.d(TAG, "onGroupFetchSuccess: 207");
            groupNames.remove("Select Group");


            /**
             * Get id of group name selectedName
             */
            for (Group group : mGroups) {
                if (group.getGroupName().equals(selectedName)){

                    /**
                     * Fetch all customer phones assigned to the selected group
                     */
                    Log.d(TAG, "onGroupFetchSuccess: fetching phones of group "+ group.getGroupName() + group.groupId);
                    customerPhonesFetchrogress.setVisibility(View.VISIBLE);
                    progressTxt.setVisibility(View.VISIBLE);
                    mSelectedGroupId = group.getGroupId();
                    presenter.fetchCustomersPhoneByGroupId(mSelectedGroupId);

                }
            }
        });

    }

    @Override
    public void onGroupFetchError() {
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                presenter.fetchGroups();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onSendMessageSuccess() {
        Toast.makeText(getActivity(), "Message sent successfully", Toast.LENGTH_SHORT).show();
        sendMsgBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSendMessageError() {
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                sendMsgBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {

            }
        });

        progressBar.setVisibility(View.GONE);
        sendMsgBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCustomersPhoneFetchSuccesss(List<String> phones) {
        customerPhonesFetchrogress.setVisibility(View.GONE);
        progressTxt.setVisibility(View.GONE);
        Log.d(TAG, "onCustomersPhoneFetchSuccesss: "+ phones);
        mPhoneList = new ArrayList<>();
        mPhoneList.addAll(phones);
        sendMsgBtn.setClickable(true);
        sendMsgBtn.setAlpha(1);
        Log.d(TAG, "onCustomersPhoneFetchSuccesss: "+ phones);



    }

    @Override
    public void onCustomersPhoneFetchError() {
        Log.d(TAG, "onCustomersPhoneFetchError: ");
        customerPhonesFetchrogress.setVisibility(View.GONE);
        progressTxt.setVisibility(View.GONE);
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                presenter.fetchCustomersPhoneByGroupId(mSelectedGroupId);
                customerPhonesFetchrogress.setVisibility(View.VISIBLE);
                progressTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @OnClick(R.id.btn_send_message)
    public void onClick(View view){

        Log.d(TAG, "onClick: "+groupNames.get(spinnerGroups.getSelectedIndex()).equals("Select Group"));
        if (groupNames.get(spinnerGroups.getSelectedIndex()).equals("Select Group")) {

            Toast.makeText(getActivity(), "Please select any group", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(messageTxt.getText().toString())) {
            Toast.makeText(getActivity(), "Messagess is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }




        if (mPhoneList != null && !mPhoneList.isEmpty()) {
            sendMsgBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            presenter.sendMessage(mPhoneList,messageTxt.getText().toString());

        }
        else{
            Toast.makeText(getActivity(), "Error fetching customers! Please try again", Toast.LENGTH_SHORT).show();
        }
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

                messageTxt.setText(smsTemplates.get(spinner.getSelectedIndex()));
                smsTemplates.remove("Select template");


            }
        });



    }

    @Override
    public void onSmsTemplateFetchError() {
        Log.d(TAG, "onSmsTemplateFetchError: ");
    }

}

package com.airbyte.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.BaseView;
import com.airbyte.R;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.NetModule;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class ManageGroupFragment extends BaseFragment implements ManageGroupFragmentView, SelectCustomerListAdapter.CustomerSelectListener,GroupCombinationListAdapter.DeleteItemClickListenr, BaseView {

    //Widgets
    @BindView(R.id.spinner_groups)
    MaterialSpinner groupSpinner;
    @BindView(R.id.et_customer_name)
    EditText customerNameTxt;
    @BindView(R.id.btn_add_group)
    Button addGroupBtn;
    private List<Customer> customers;
    private List<Group> groups;
    private List<String> groupNames;
    private String currentGroupId;
    private String currentCustomerId;
    @BindView(R.id.rv_group)
    RecyclerView recyclerView;
    int deleteCounter;
    @BindView(R.id.progress_bar)
    TashieLoader tashieLoader;
    GroupCombinationListAdapter adapter;
    @BindView(R.id.pb)
    ProgressBar progressBar;
    String currentGrpDltId;
    ProgressBar dltProgress;
    ImageButton dltWorkerBtn;


    List<GroupCombination> groupCombinations;


    private static final String TAG = "ManageGroupFragment";
    AdminHomePresenter adminHomePresenter;
     @Inject
     Retrofit retrofit;
     SelectCustomerDialogFragment dialogFragment;
     AdminServicePresenter adminServicePresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
        adminHomePresenter = new AdminHomePresenter(retrofit,this);
        adminServicePresenter = new AdminServicePresenter(this,retrofit);
        adminHomePresenter.fetchCustomers();

        adminServicePresenter.getGroupCombination();
        adminServicePresenter.getGroups();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_manage_group;
    }

    @Override
    public void initView() {
        Log.d(TAG, "initView: ");
    }

    @Override
    protected void onNetworkActive() {

         adminHomePresenter.fetchCustomers();

        adminServicePresenter.getGroupCombination();
        adminServicePresenter.getGroups();
    }


    @Override
    public void onGroupFetchSuccess(List<Group> groups) {
        progressBar.setVisibility(View.GONE);
        groupNames = new ArrayList<>();
        if (groups != null && !groups.isEmpty()){
             this.groups = groups;
             for (Group group : groups) {
                groupNames.add(group.getGroupName());
            }

            groupSpinner.setItems(groupNames);
        }

        Log.d(TAG, "onGroupFetchSuccess: "+ this.groups);
    }
    
    @OnClick({R.id.et_customer_name,R.id.btn_add_group})
    public void onClick(View view){

        switch (view.getId()) {

            case R.id.et_customer_name:
                Log.d(TAG, "onClick: "+ customers);

                if (customers == null) {
                    Log.d(TAG, "onClick: ");
                    showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
                        @Override
                        public void onRetry() {
                            adminHomePresenter.fetchCustomers();
                            return;
                        }

                        @Override
                        public void onCancel() {
                            return;

                        }
                    });
                    return;
                 }

                dialogFragment = new SelectCustomerDialogFragment(this.customers,this::onCustomerSelected);
                dialogFragment.show(getChildFragmentManager(),"dialog");
                break;

            case R.id.btn_add_group:
                addNewGroup();


        }


     }

    private void addNewGroup() {

        if (customerNameTxt.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select a customer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }

        //Get group id of selected group name
        //get customer id of selected customer

        String groupName = groupNames.get(groupSpinner.getSelectedIndex());
        for (Group group : groups) {
            if (groupName.equals(group.getGroupName())){
                currentGroupId = group.getGroupId();
            }
        }


        tashieLoader.setVisibility(View.VISIBLE);
        addGroupBtn.setVisibility(View.GONE);

        adminServicePresenter.addNewGroupCombination(currentGroupId,currentCustomerId);
    }

    @Override
    public void onUserListFetchSuccess(List<Customer> customerList) {
        if (customerList != null && !customerList.isEmpty()){
            this.customers = customerList;
        }
        Log.d(TAG, "onUserListFetchSuccess: "+ this.customers);
    }


    @Override
    public void onGroupFetchFailed() {
        Log.d(TAG, "onGroupFetchFailed: ");
        progressBar.setVisibility(View.GONE);
        showConnectionErrorPopup(getActivity(),new NetworkErrorDialogCallback(){

            @Override
            public void onRetry() {
                adminHomePresenter.fetchCustomers();
                adminServicePresenter.getGroupCombination();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Dismiss CLicked");
            }
        });
        Log.d(TAG, "onGroupFetchFailed: ");
    }


    @Override
    public void onNewGroupAddSuccess() {
        tashieLoader.setVisibility(View.GONE);
        addGroupBtn.setVisibility(View.VISIBLE);


        Toast.makeText(getActivity(), "Group Added Successfully", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onNewGroupAddSuccess: ");
         customerNameTxt.getText().clear();
        adminServicePresenter.getGroupCombination();
    }

    @Override
    public void onNewGroupAddFailed() {
        tashieLoader.setVisibility(View.GONE);
        addGroupBtn.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Unexpected error occured, Cross check your internet connection", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onNewGroupAddFailed: ");
    }

    @Override
    public void onGroupDeleteSuccess() {
        dltProgress.setVisibility(View.GONE);
        dltWorkerBtn.setVisibility(View.VISIBLE);
        Log.d(TAG, "onGroupDeleteSuccess: ");
        groupCombinations.remove(deleteCounter);
        adapter.notifyItemRemoved(deleteCounter);
    }

    @Override
    public void onGroupDeleteFailed() {
        Log.d(TAG, "onGroupDeleteFailed: ");
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                dltWorkerBtn.setVisibility(View.GONE);
                dltProgress.setVisibility(View.VISIBLE);
                adminServicePresenter.deleteGroupCombinationById(currentGrpDltId);
            }

            @Override
            public void onCancel() {

            }
        });
        dltProgress.setVisibility(View.GONE);
        dltWorkerBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onGroupCombinationFetchSuccess(List<GroupCombination> groupCombinations) {
        this.groupCombinations = groupCombinations;
         adapter  = new GroupCombinationListAdapter(groupCombinations,this::onItemDeleteClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onGroupCombinationFetchError() {
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                adminServicePresenter.getGroupCombination();
            }

            @Override
            public void onCancel() {

            }
        });
    }


    @Override
    public void onUserListFetchFailed() {
        Log.d(TAG, "onUserListFetchFailed: ");
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        currentCustomerId = customer.getCustomerId();
        customerNameTxt.setText(customer.getCustomerName() + "  -  "+ customer.getCustomerPhone1() );
    }

    @Override
    public void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton imageButton) {
        deleteCounter = position;

        currentGrpDltId = groupCombinations.get(position).getGroupCombinationId();
        this.dltProgress = progressBar;
        this.dltWorkerBtn = imageButton;
        dltWorkerBtn.setVisibility(View.GONE);
        dltProgress.setVisibility(View.VISIBLE);
        Log.d(TAG, "onItemDeleteClicked: "+ currentGrpDltId);

        adminServicePresenter.deleteGroupCombinationById(currentGrpDltId);
    }
}

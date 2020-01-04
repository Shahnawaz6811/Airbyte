package com.airbyte.admin;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroupFragment extends BaseFragment implements GroupListAdapter.DeleteItemClickListenr,AddGroupFragmentView {
    private static final String TAG = "AddGroupFragment";
    AdminServicePresenter presenter;
    @Inject
    Retrofit retrofit;
    @BindView(R.id.rv_group)
    RecyclerView recyclerView;
    @BindView(R.id.et_group_name)
    EditText groupNameTxt;
    @BindView(R.id.btn_add_group)
    Button addGrpButton;
    @BindView(R.id.progress_bar)
    TashieLoader progress;
    GroupListAdapter adapter;
    List<Group> groupList;
    int deleteCounter ;
    @BindView(R.id.pb)
    ProgressBar progressBar;
    ProgressBar deleteProgress;
    ImageButton grpDltBtn;
    String currentGroupDltId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupList = new ArrayList<>();
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
        presenter = new AdminServicePresenter(this,retrofit);
        presenter.getGroups();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        return view;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_add_group;
    }

    @Override
    public void initView() {
     }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "onViewCreated: ");

    }

    @Override
    public void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton imageButton) {
        deleteCounter = position;
        this.deleteProgress =  progressBar;
        this.grpDltBtn = imageButton;
        currentGroupDltId = groupList.get(position).groupId;
        Log.d(TAG, "onItemDeleteClicked: "+ currentGroupDltId);

        presenter.deleteGroupById(currentGroupDltId);
    }

    @Override
    public void onGroupFetchSuccess(List<Group> groups) {

             groupList.clear();
             groupList = groups;
            adapter = new GroupListAdapter(groups,this::onItemDeleteClicked);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
            recyclerView.setHasFixedSize(true);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);

    }

    @Override
    public void onGroupFetchFailed() {

        Log.d(TAG, "onGroupFetchFailed: ");
        progressBar.setVisibility(View.GONE);
       showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
           @Override
           public void onRetry() {
               progressBar.setVisibility(View.VISIBLE);
               presenter.getGroups();
           }

           @Override
           public void onCancel() {

           }
       });
}

    @Override
        public void onNewGroupAddSuccess() {
         addGrpButton.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "New Group added", Toast.LENGTH_SHORT).show();
        presenter.getGroups();

    }

    @Override
    public void onNewGroupAddFailed() {
          addGrpButton.setVisibility(View.VISIBLE);
         showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
             @Override
             public void onRetry() {
                 if (groupNameTxt.getText().toString().isEmpty()) {
                     groupNameTxt.setError("Group name is empty");
                     return;
                 }

                 presenter.addNewGroup(groupNameTxt.getText().toString());
                 addGrpButton.setVisibility(View.GONE);
                 progress.setVisibility(View.VISIBLE);
             }

             @Override
             public void onCancel() {

             }
         });
    }

    @Override
    public void onGroupDeleteSuccess() {
        Log.d(TAG, "onGroupDeleteSuccess: ");
        groupList.remove(deleteCounter);
        adapter.notifyItemRemoved(deleteCounter);
        deleteProgress.setVisibility(View.GONE);
        grpDltBtn.setVisibility(View.VISIBLE);

//        recyclerView.setAdapter(groupList);
    }

    @Override
    public void onResume() {
        super.onResume();
     }


    @Override
    protected void onNetworkActive() {
        progressBar.setVisibility(View.VISIBLE);
        presenter.getGroups();

    }


    @Override
    public void onGroupDeleteFailed() {
         showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
             @Override
             public void onRetry() {
                 grpDltBtn.setVisibility(View.GONE);
                 deleteProgress.setVisibility(View.VISIBLE);
                 presenter.deleteGroupById(currentGroupDltId);
             }

             @Override
             public void onCancel() {

             }
         });
         deleteProgress.setVisibility(View.GONE);
        grpDltBtn.setVisibility(View.VISIBLE);

     }

    @Override
    public void onGroupCombinationFetchSuccess(List<GroupCombination> groupCombinations) {

    }

    @Override
    public void onGroupCombinationFetchError() {

    }

    @OnClick(R.id.btn_add_group)
    public void onClick(View view) {
        if (groupNameTxt.getText().toString().isEmpty()) {
            groupNameTxt.setError("Group name is empty");
            return;
        }

        if(!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }

        progress.setVisibility(View.VISIBLE);
        addGrpButton.setVisibility(View.GONE);
        presenter.addNewGroup(groupNameTxt.getText().toString());
    }
}

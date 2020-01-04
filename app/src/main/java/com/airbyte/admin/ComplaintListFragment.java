package com.airbyte.admin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbyte.R;
import com.airbyte.customer.complaints.Complaint;
import com.airbyte.verification.BaseFragment;

import java.util.List;

import javax.inject.Inject;



public class ComplaintListFragment extends BaseFragment implements ComplaintFragmentListView, ComplaintListAdapter.ComplaintItemClickListenr {
    private static final String TAG = "ComplaintListFragment";
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ComplaintListAdapter adapter;
    private AdminHomePresenter presenter;
    @Inject
     Retrofit retrofit;
    AlertDialog alertDialog;
    @BindView(R.id.pb)
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
          presenter = new AdminHomePresenter(retrofit,this);
          presenter.fetchComplaints();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
     }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_complaint_list;
    }

    @Override
    public void initView() {
        Log.d(TAG, "initView: ");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: ");
                presenter.fetchComplaints();
             }
        });

    }

    @Override
    protected void onNetworkActive() {
        progressBar.setVisibility(View.VISIBLE);
        presenter.fetchComplaints();
    }

    @Override
    public void onComplaintListFetchSuccess(List<Complaint> complaintList) {
         Log.d(TAG, "onComplaintListFetchSuccess: ");
        progressBar.setVisibility(View.GONE);

         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setAdapter(new ComplaintListAdapter(this,complaintList));
    }

    @Override
    public void onComplaintListFetchFailed() {
        Log.d(TAG, "onComplaintListFetchFailed: ");
        progressBar.setVisibility(View.GONE);
        showConnectionErrorPopup(getActivity(),new NetworkErrorDialogCallback(){

            @Override
            public void onRetry() {
                progressBar.setVisibility(View.VISIBLE);
                presenter.fetchComplaints();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Dismiss CLicked");
            }
        });

        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public void onComplaintItemClicked(Complaint complaint) {
        ComplaintDialogFragment dialogFragment = new ComplaintDialogFragment();
         Bundle bundle = new Bundle();
        bundle.putParcelable("complaint",complaint);
        dialogFragment.setArguments(bundle);
        FragmentManager fm = getChildFragmentManager();
        dialogFragment.show(fm,"complaint");


    }
}

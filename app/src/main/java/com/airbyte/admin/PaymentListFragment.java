package com.airbyte.admin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
 import com.airbyte.verification.BaseFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentListFragment extends BaseFragment implements PaymentFragmentListView, PaymentListAdapter.PaymentItemClickListener {

    private static final String TAG = "ManageGroupFragment";

    @BindView(R.id.pb)
    ProgressBar progressBar;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    AdminHomePresenter presenter;
    PaymentListAdapter adapter;
    @Inject
    Retrofit retrofit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
        presenter = new AdminHomePresenter(retrofit,this);
        presenter.fetchPayments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 60");
        View view = super.onCreateView(inflater,container,savedInstanceState);
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
        return R.layout.fragment_payment_list;
    }

    @Override
    public void initView() {

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "onRefresh: ");
            presenter.fetchPayments();
         });

     }

    @Override
    protected void onNetworkActive() {
        progressBar.setVisibility(View.VISIBLE);
        presenter.fetchPayments();
    }

    @Override
    public void onPaymentListFetchSuccess(List<Payment> paymentList) {
        Log.d(TAG, "onPaymentListFetchSuccess: ");
        progressBar.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setRefreshing(false);
        Log.d(TAG, "onPaymentListFetchSuccess: 92");
        recyclerView.setAdapter(new PaymentListAdapter(this,paymentList));

    }

    @Override
    public void onPaymentListFetchFailed() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        showConnectionErrorPopup(getActivity(),new NetworkErrorDialogCallback(){

            @Override
            public void onRetry() {
                progressBar.setVisibility(View.VISIBLE);
                presenter.fetchPayments();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Dismiss CLicked");
            }
        });

    }

    @Override
    public void onPaymentItemClick(Payment payment) {

        PaymentDialogFragment dialogFragment = new PaymentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("payment",payment);
        dialogFragment.setArguments(bundle);
        FragmentManager fm = getChildFragmentManager();
        dialogFragment.show(fm,"complaint");
    }
}

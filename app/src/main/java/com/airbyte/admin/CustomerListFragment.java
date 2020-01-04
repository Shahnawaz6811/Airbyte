package com.airbyte.admin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerListFragment extends BaseFragment implements CustomerFragmentListView,
        CustomerListAdapter.CustomerItemClickListener,SearchView.OnQueryTextListener,CustomerDialogFragment.CustomerUpdateListener {
    private static final String TAG = "CustomerListFragment";
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
     private AdminHomePresenter presenter;
       @Inject
    Retrofit retrofit;
    AlertDialog alertDialog;
    @BindView(R.id.pb)
    ProgressBar progressBar;
     CustomerListAdapter adapter;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
        presenter = new AdminHomePresenter(retrofit,this);
        presenter.fetchCustomers();
        setHasOptionsMenu(true);
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
        progressBar.setVisibility(View.VISIBLE);
     }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_complaint_list;
    }

    @Override
    public void initView() {

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "onRefresh: ");
            presenter.fetchCustomers();
        });

    }

    @Override
    protected void onNetworkActive() {
        Log.d(TAG, "onNetworkActive: ");
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        progressBar.setVisibility(View.VISIBLE);
        presenter.fetchCustomers();
    }

    @Override
    public void onUserListFetchSuccess(List<Customer> customerList) {
        Log.d(TAG, "onUserListFetchSuccess: ");
        progressBar.setVisibility(View.GONE);

        adapter = new CustomerListAdapter(customerList,this::onCustomerItemClick);

        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onUserListFetchFailed() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        showConnectionErrorPopup(getActivity(),new NetworkErrorDialogCallback(){

            @Override
            public void onRetry() {
                progressBar.setVisibility(View.VISIBLE);
                presenter.fetchCustomers();
             }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Dismiss CLicked");
            }
        });
        Log.d(TAG, "onGroupFetchFailed: ");
    }



    @Override
    public void onCustomerItemClick(Customer customer) {
        CustomerDialogFragment dialogFragment = new CustomerDialogFragment();
        dialogFragment.setCustomerUpdateListener(this::customerUpdateSuccess);
        dialogFragment.setCancelable(false);
        Bundle bundle = new Bundle();
        bundle.putParcelable("customer",customer);
        dialogFragment.setArguments(bundle);
        FragmentManager fm = getChildFragmentManager();
        dialogFragment.show(fm,"complaint");


        }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.searchBar);
         SearchView searchView = (SearchView) searchItem.getActionView();
         searchView.clearFocus();
         searchView.setMaxWidth(SearchView.LayoutParams.MATCH_PARENT);
         searchView.setQueryHint("Search by name or phone number");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: ");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: ");
        adapter.filter(newText);
        return false;
    }

    @Override
    public void customerUpdateSuccess() {
        presenter.fetchCustomers();
    }
}

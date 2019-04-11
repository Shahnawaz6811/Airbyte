package com.airbyte.admin;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class AddWorkerFragment extends BaseFragment implements AddWorkerFragmentView,WorkerListAdapter.DeleteItemClickListenr{
    private static final String TAG = "AddWorkerFragment";

    @BindView(R.id.et_worker_name)
    EditText workerNameText;
    @BindView(R.id.et_worker_phone1)
    EditText workerPhone1Text;
    @BindView(R.id.et_worker_phone2)
    EditText workerPhone2Text;
     @BindView(R.id.btn_add_worker)
    Button addWorkerBtn;
    @BindView(R.id.progress_bar)
    TashieLoader progress;
    AdminServicePresenter presenter;
    @Inject
    Retrofit retrofit;
    @BindView(R.id.rv_workers)
    RecyclerView recyclerView;
    List<Worker> workers;
    WorkerListAdapter adapter;
    int deleteCounter ;
    ProgressBar dltProgress;
    ImageButton dltWorkerBtn;
    String currentWorkerDltId;
    @BindView(R.id.pb)
    ProgressBar progressBar;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerBuilder(AdminApiService.baseUrl,this).build().inject(this);
        presenter = new AdminServicePresenter(this,retrofit);
        workers = new ArrayList<>();
        presenter.getWorkers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_add_worker;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onNetworkActive() {

        presenter.getWorkers();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onWorkerAddSuccess() {
        addWorkerBtn.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Worker Added successfully", Toast.LENGTH_SHORT).show();
        presenter.getWorkers();
    }

    @Override
    public void onWorkerAddFailed(String message) {
        Log.d(TAG, "onWorkerAddFailed: ");
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        addWorkerBtn.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton btn) {
        deleteCounter = position;
        this.dltProgress = progressBar;
        this.dltWorkerBtn = btn;
        dltWorkerBtn.setVisibility(View.GONE);
        dltProgress.setVisibility(View.VISIBLE);
        currentWorkerDltId = workers.get(position).getId();
        Log.d(TAG, "onItemDeleteClicked: "+ currentWorkerDltId);

        presenter.deleteWorkerById(currentWorkerDltId);
    }

    @Override
    public void onWorkerFetchSuccess(List<Worker> workers) {
        progressBar.setVisibility(View.GONE);
        this.workers.clear();
        this.workers = workers;
        adapter = new WorkerListAdapter(workers,this::onItemDeleteClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onWorkerFetchFailed() {
        progressBar.setVisibility(View.GONE);
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                progressBar.setVisibility(View.VISIBLE);
                presenter.getWorkers();
            }

            @Override
            public void onCancel() {

            }
        });
     }

    @Override
    public void onWorkerDeleteSuccess() {
        dltProgress.setVisibility(View.GONE);
        dltWorkerBtn.setVisibility(View.VISIBLE);
        workers.remove(deleteCounter);
        adapter.notifyItemRemoved(deleteCounter);
    }

    @Override
    public void onWorkerDeleteFailed() {
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                dltWorkerBtn.setVisibility(View.GONE);
                dltProgress.setVisibility(View.VISIBLE);
                presenter.deleteWorkerById(currentWorkerDltId);
            }

            @Override
            public void onCancel() {

            }
        });
        dltProgress.setVisibility(View.GONE);
        dltWorkerBtn.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.btn_add_worker)
    public void onClick(View view) {

        validateFields();
    }

    private void validateFields() {

        if (workerNameText.getText().toString().isEmpty()) {
            workerNameText.setError("Worker name  can't be empty");
            return;
        }


        if (!workerPhone1Text.getText().toString().isEmpty() && workerPhone1Text.getText().toString().length() < 10 ) {
            workerPhone1Text.setError("Phone number should be 10 digit long");
            return;
        }


        if (!workerPhone2Text.getText().toString().isEmpty() && workerPhone2Text.getText().toString().length() < 10 ) {
            workerPhone2Text.setError("Phone number should be 10 digit long");
            return;
        }

        if (workerPhone1Text.getText().toString().isEmpty()){
            workerPhone1Text.setError("Phone number can't be empty");
            return;
        }




        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }

        presenter = new AdminServicePresenter(this,retrofit);
        progress.setVisibility(View.VISIBLE);
        addWorkerBtn.setVisibility(View.GONE);
        presenter.addNewWorker(workerNameText.getText().toString(),workerPhone1Text.getText().toString(),workerPhone2Text.getText().toString());



    }
}

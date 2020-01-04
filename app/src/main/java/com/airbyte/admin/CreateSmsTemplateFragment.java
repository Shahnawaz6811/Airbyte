package com.airbyte.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.BaseView;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.verification.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateSmsTemplateFragment extends BaseFragment implements
        SmsTemplateView,BaseView,TemplateListAdapter.DeleteItemClickListenr {
    private static final String TAG = "CreateSmsTemplateFragme";
    @BindView(R.id.button)
    Button addTemplateBtn;
    @BindView(R.id.progress_bar)
    TashieLoader progress;
    @BindView(R.id.et_templates)
    EditText templateTxt;
    @BindView(R.id.pb_load_template)
    ProgressBar loadTemplateProgress;
    TemplateFragmentPresenter presenter;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
     TemplateListAdapter adapter;
    List<Template> templates = new ArrayList<>();
    ProgressBar dltProgress;
    ImageButton dltIb;
    Retrofit retrofit;


      static CreateSmsTemplateFragment newInstance() {

        Bundle args = new Bundle();
        args.putString("title","Sms");

        CreateSmsTemplateFragment fragment = new CreateSmsTemplateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          retrofit = new Retrofit.Builder().baseUrl(AdminApiService.baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

  
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTemplateProgress.setVisibility(View.VISIBLE);
        presenter = new TemplateFragmentPresenter(retrofit,this);
        presenter.getSmsTemplates();
    }

    @Override
    public void onSmsTemplateFetchSuccess(List<Template> templates) {
        Log.d(TAG, "onTemplateFetchSuccess: ");
        this.templates.clear();
        this.templates = templates;
        loadTemplateProgress.setVisibility(View.GONE);
        adapter = new TemplateListAdapter(templates,this::onItemDeleteClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),RecyclerView.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSmsTemplateFetchFailed() {
        Log.d(TAG, "onTemplateFetchFailed: ");
        loadTemplateProgress.setVisibility(View.GONE);
        showConnectionErrorPopup(getActivity(), new NetworkErrorDialogCallback() {
            @Override
            public void onRetry() {
                loadTemplateProgress.setVisibility(View.VISIBLE);
                presenter.getSmsTemplates();
            }

            @Override
            public void onCancel() {

            }
        });

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_create_sms_template;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onNetworkActive() {
        if (templates != null && templates.isEmpty()) {
            loadTemplateProgress.setVisibility(View.VISIBLE);
            presenter.getSmsTemplates();
        }
    }

    @OnClick(R.id.button)
    public void onClick(View view){
          if (!NetworkUtil.isNetworkAvailable(getActivity())){
              AppUtils appUtils = new AppUtils();
              appUtils.showNetworkErrorDialog(getActivity());
              return;
          }
          String template = templateTxt.getText().toString();
          if (template.isEmpty()) {
              Toast.makeText(getActivity(), "Template field is empty", Toast.LENGTH_SHORT).show();
              return;
          }
        addTemplateBtn.setVisibility(View.GONE);
         progress.setVisibility(View.VISIBLE); 
        presenter.addSmsTemplate(template);
    }

    @Override
    public void onSmsTemplateAddSuccess() {
        Toast.makeText(getActivity(), "New template added", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTemplateAddSuccess: ");
        addTemplateBtn.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        Log.d(TAG, "onTemp155lateAddSuccess: ");
        presenter.getSmsTemplates();
    }

    @Override
    public void onSmsTemplateAddFailed() {
        Toast.makeText(getActivity(), "Unexpected error occured, Please check your internet connection", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onTemplateAddFailed: ");
        addTemplateBtn.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }


    @Override
    public void onSmsTemplateDeleteSuccess() {
        Toast.makeText(getActivity(), "Template deleted successfully", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onTemplateDeleteSuccess: ");
        dltIb.setVisibility(View.VISIBLE);
        dltProgress.setVisibility(View.GONE);
        presenter.getSmsTemplates();
    }

    @Override
    public void onSmsTemplateDeleteFailed() {
        Toast.makeText(getActivity(), "Unexpected error occured, Please check your internet connection", Toast.LENGTH_SHORT).show();
        dltIb.setVisibility(View.VISIBLE);
        dltProgress.setVisibility(View.GONE);

    }

    @Override
    public void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton imageButton) {

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            AppUtils appUtils = new AppUtils();
            appUtils.showNetworkErrorDialog(getActivity());
            return;
        }


        this.dltProgress = progressBar;
        this.dltIb = imageButton;
        dltProgress.setVisibility(View.VISIBLE);
        dltIb.setVisibility(View.GONE);
        String id = templates.get(position).getId();
        Log.d(TAG, "onItemDeleteClicked: "+ id);
        presenter.deleteSmsTemplate(id);
    }
}

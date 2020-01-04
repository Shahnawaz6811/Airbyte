package com.airbyte.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbyte.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public  class WorkerListAdapter extends RecyclerView.Adapter<WorkerListAdapter.ViewHolder> {

    private List<Worker> workers;
    private GroupListAdapter.DeleteItemClickListenr deleteItemClickListenr;


    interface DeleteItemClickListenr {
        void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton imageButton);
    }

      WorkerListAdapter(List<Worker> workers, GroupListAdapter.DeleteItemClickListenr listenr) {
        this.workers = workers;
          this.deleteItemClickListenr = listenr;

      }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_worker,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.workerName.setText(workers.get(position).getWorkerName());
        holder.workerPhone1.setText(workers.get(position).getWorkerPhone1());
        holder.workerPhone2.setText(workers.get(position).getGetWorkerPhone2());
     }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_worker_name)
        TextView workerName;
        @BindView(R.id.tv_worker_phone1)
                TextView workerPhone1;
        @BindView(R.id.tv_worker_phone2)
        TextView workerPhone2;
        @BindView(R.id.ib_delete)
        ImageButton deleteGrpBtn;
        @BindView(R.id.pb)
        ProgressBar progressBar;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            deleteGrpBtn.setOnClickListener(v -> deleteItemClickListenr.onItemDeleteClicked(getAdapterPosition(),progressBar,deleteGrpBtn));


        }
    }
}

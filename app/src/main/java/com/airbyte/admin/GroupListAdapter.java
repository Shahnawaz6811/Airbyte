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

public  class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<Group> groups;

    private DeleteItemClickListenr deleteItemClickListenr;

    interface DeleteItemClickListenr {
        void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton imageButton);
    }
      GroupListAdapter(List<Group> groups,DeleteItemClickListenr listenr) {
        this.groups = groups;
        this.deleteItemClickListenr = listenr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_group,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.groupId.setText(groups.get(position).groupId);
        holder.groupName.setText(groups.get(position).getGroupName());
     }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_group_id)
        TextView groupId;
        @BindView(R.id.tv_group_name)
                TextView groupName;
        @BindView(R.id.ib_delete)
        ImageButton deleteGrpBtn;
        @BindView(R.id.pb)
                ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            deleteGrpBtn.setOnClickListener(v -> {
                progressBar.setVisibility(View.VISIBLE);
                deleteGrpBtn.setVisibility(View.GONE);
                deleteItemClickListenr.onItemDeleteClicked(getAdapterPosition(), progressBar,deleteGrpBtn);
            });

        }
    }
}

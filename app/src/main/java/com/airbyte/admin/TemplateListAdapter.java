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

public  class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.ViewHolder> {

    private List<Template> templates;

    private DeleteItemClickListenr deleteItemClickListenr;

    interface DeleteItemClickListenr {
        void onItemDeleteClicked(int position, ProgressBar progressBar, ImageButton imageButton);
    }
      TemplateListAdapter(List<Template> templates, DeleteItemClickListenr listenr) {
        this.templates = templates;
        this.deleteItemClickListenr = listenr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_template,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.template.setText(templates.get(position).getTemplate());
      }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.template_txt)
        TextView template;
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

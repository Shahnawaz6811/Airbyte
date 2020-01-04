package com.airbyte.admin;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbyte.R;
import com.airbyte.customer.complaints.Complaint;
import com.airbyte.customer.payment.Payment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.ViewHolder> {

    private List<Complaint> complaintList;
    private static final String TAG = "ComplaintListAdapter";

    private ComplaintItemClickListenr listener;
    interface ComplaintItemClickListenr {
        void onComplaintItemClicked(Complaint complaint);
    }

     ComplaintListAdapter(ComplaintItemClickListenr listener,List<Complaint> complaints) {

         Log.d(TAG, "ComplaintListAdapter: ");
         complaintList = complaints;
         this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_complaint,null,false);
        Log.d(TAG, "onCreateViewHolder: ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
            holder.complaintId.setText(complaintList.get(position).getComplaintId());
            holder.complaintPhone.setText(complaintList.get(position).getCustomerPhone());
            holder.customerId.setText(complaintList.get(position).getCustomerId());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+complaintList.size());
        return complaintList.size();
    }

      void refreshAdapter(List<Complaint> complaints){
        Log.d(TAG, "refreshAdapter: ");
        complaintList.clear();
        complaintList.addAll(complaints);
          Log.d(TAG, "refreshAdapter: "+complaintList);
        notifyDataSetChanged();

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_complaint_id)
        TextView complaintId;
        @BindView(R.id.tv_complaint_phone)
        TextView complaintPhone;
        @BindView(R.id.tv_customer_id)
        TextView customerId;
        @BindView(R.id.root_layout)
        CardView rootLayout;

          ViewHolder(@NonNull View itemView) {
            super(itemView);
              ButterKnife.bind(this,itemView);

              rootLayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Log.d(TAG, "onClick: ");
                      listener.onComplaintItemClicked(complaintList.get(getAdapterPosition()));
                  }
              });
        }





    }
}

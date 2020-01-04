package com.airbyte.admin;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbyte.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ViewHolder> {
    private static final String TAG = "PaymentListAdapter";
    private List<Payment> paymentResponse;
    PaymentItemClickListener listener;



      PaymentListAdapter(PaymentItemClickListener listener,List<Payment> paymentResponse) {
        this.paymentResponse = paymentResponse;
        this.listener = listener;
    }

    public interface PaymentItemClickListener {
        void onPaymentItemClick(Payment payment);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_payment,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+paymentResponse.get(position).getCustomerId());
        holder.customerId.setText(paymentResponse.get(position).getCustomerId());
        Log.d(TAG, "onBindViewHolder: "+ paymentResponse.get(position).getPhone1());
        holder.customerPhone.setText(paymentResponse.get(position).getPhone1());
        holder.paymentAmount.setText("INR "+paymentResponse.get(position).getPaymentAmount());
     }


    @Override
    public int getItemCount() {
        return paymentResponse.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_customer_id)
        TextView customerId;
        @BindView(R.id.tv_customer_phone)
        TextView customerPhone;
        @BindView(R.id.tv_payment_amount)
        TextView paymentAmount;
        @BindView(R.id.root_layout)
        CardView rootLayout;

          ViewHolder(@NonNull View itemView) {
            super(itemView);
              ButterKnife.bind(this,itemView);

              rootLayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      listener.onPaymentItemClick(paymentResponse.get(getAdapterPosition()));
                  }
              });

          }


    }
}

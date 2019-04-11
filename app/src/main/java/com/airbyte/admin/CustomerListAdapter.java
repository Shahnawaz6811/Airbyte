package com.airbyte.admin;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbyte.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ViewHolder> {

    private List<Customer> customers;
    private List<Customer> copyList = new ArrayList<>();
    private static final String TAG = "CustomerListAdapter";
    private CustomerItemClickListener listener;
    public interface CustomerItemClickListener {
       void onCustomerItemClick(Customer customer);
    }

      CustomerListAdapter(List<Customer> customers,CustomerItemClickListener listener) {
        this.customers = customers;
        this.copyList.addAll(customers);
        this.listener = listener;

    }

    public void filter(String text){
        Log.d(TAG, "filter: ");
        
        customers.clear();
        for (Customer customer : copyList) {
            Log.d(TAG, "filter: 43");
            if (customer.getCustomerName().toLowerCase().contains(text.toLowerCase()) || customer.getCustomerPhone1().contains(text) || customer.getCustomerPhone2().contains(text)) {
                  customers.add(customer);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_user,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.customerId.setText(customers.get(position).getCustomerId());
        holder.customerName.setText(customers.get(position).getCustomerName());
        holder.customerPhone.setText(customers.get(position).getCustomerPhone1());
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";
        @BindView(R.id.root_layout)
        CardView rootLayout;
        @BindView(R.id.tv_customer_id)
        TextView customerId;
        @BindView(R.id.tv_customer_name)
        TextView customerName;
        @BindView(R.id.tv_customer_phone)
        TextView customerPhone;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    listener.onCustomerItemClick(customers.get(getAdapterPosition()));
                }
            });



        }
    }
}

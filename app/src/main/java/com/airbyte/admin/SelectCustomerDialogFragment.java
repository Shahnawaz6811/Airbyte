package com.airbyte.admin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.airbyte.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectCustomerDialogFragment extends DialogFragment implements SelectCustomerListAdapter.CustomerSelectListener {

    SelectCustomerListAdapter adapter;
    List<Customer> customers;
    @BindView(R.id.lv_select_customer)
    ListView listView;
    SelectCustomerListAdapter.CustomerSelectListener listener;

    public SelectCustomerDialogFragment(List<Customer> list, SelectCustomerListAdapter.CustomerSelectListener listener) {
        this.customers = list;
        this.listener = listener;

    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.select_customer_dialog_height);
        getDialog().getWindow().setLayout(width,height);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_select_customer,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final View decorView = getDialog()
                .getWindow()
                .getDecorView();

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
        scaleDown.setDuration(500);
        scaleDown.start();




//        dismissBtn.setOnClickListener(v -> {
//            final View decorView1 = getDialog()
//                    .getWindow()
//                    .getDecorView();
//
//            ObjectAnimator scaleDown1 = ObjectAnimator.ofPropertyValuesHolder(decorView1,
//                    PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f),
//                    PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f),
//                    PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f));
//            scaleDown1.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    dismiss();
//                }
//
//                @Override
//                public void onAnimationStart(Animator animation) {
//                }
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                }
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                }
//            });
//            scaleDown1.setDuration(300);
//            scaleDown1.start();
//        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SelectCustomerListAdapter(customers,this::onCustomerSelected);
        listView.setAdapter(adapter);
    }


    @Override
    public void onCustomerSelected(Customer customer) {
         listener.onCustomerSelected(customer);
         dismiss();
    }
}

class SelectCustomerListAdapter extends BaseAdapter {
    private static final String TAG = "SelectCustomerListAdapt";
    List<Customer> customers;
    CustomerSelectListener listener;
    public interface CustomerSelectListener {
        void onCustomerSelected(Customer customer);
    }

    public SelectCustomerListAdapter(List<Customer> customers, CustomerSelectListener listener) {
        this.customers = customers;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return customers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_customer,null,false);
        }

        TextView customerNameTxt = convertView.findViewById(R.id.tv_customer_name);
        TextView customerPhoneTxt = convertView.findViewById(R.id.tv_customer_phone);
        RadioButton radioButton = convertView.findViewById(R.id.rb_customer);

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButton.setChecked(true);
                    Log.d(TAG, "onCheckedChanged: ");
                    listener.onCustomerSelected(customers.get(position));
                }
            }
        });

        customerNameTxt.setText(customers.get(position).getCustomerName());
        customerPhoneTxt.setText(customers.get(position).getCustomerPhone1());
        return convertView;
    }
}

package com.airbyte.admin;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbyte.R;
import com.airbyte.verification.BaseFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class CreateTemplateFragment extends BaseFragment {
    private static final String TAG = "SendMessageFragment";
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    List<Fragment> fragmentList;
    ViewPagerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_create_template;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initView() {

        //attach tabs with viewpager
        tabLayout.setupWithViewPager(viewPager);

        addFragments();
        adapter = new ViewPagerAdapter(getChildFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);


     }

    private void addFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(CreateSmsTemplateFragment.newInstance());
        fragmentList.add(CreateComplaintTemplateFragment.newInstance());
     }

    @Override
    protected void onNetworkActive() {

    }



    static class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;

        ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getArguments().getString("title");
        }
    }
}

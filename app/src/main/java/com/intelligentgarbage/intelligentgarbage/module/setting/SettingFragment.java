package com.intelligentgarbage.intelligentgarbage.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.intelligentgarbage.intelligentgarbage.R;
import com.intelligentgarbage.intelligentgarbage.module.base.BaseFragment;
import com.intelligentgarbage.intelligentgarbage.module.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by null on 17-5-6.
 */

public class SettingFragment extends BaseFragment<SettingFragmentView, SettingFragmentPresenter> implements SettingFragmentView {
    @BindView(R.id.user_manage)
    LinearLayout userManage;
    @BindView(R.id.about)
    LinearLayout about;
    @BindView(R.id.check_upgrade)
    LinearLayout checkUpgrade;
    @BindView(R.id.help_and_feedback)
    LinearLayout helpAndFeedback;
    @BindView(R.id.exit)
    LinearLayout exit;
    Unbinder unbinder;

    @Override
    protected SettingFragmentPresenter createPresenter() {
        return new SettingFragmentPresenter();
    }

    @Override
    protected int createViewLayoutId() {
        return R.layout.fragment_setting;
    }

    public static SettingFragment newInstance() {
        SettingFragment settingFragment = new SettingFragment();
        return settingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        userManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserManage();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setUserManage(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}

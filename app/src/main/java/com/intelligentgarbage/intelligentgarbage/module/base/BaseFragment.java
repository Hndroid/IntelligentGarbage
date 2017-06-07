package com.intelligentgarbage.intelligentgarbage.module.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelligentgarbage.intelligentgarbage.R;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by null on 17-4-29.
 */

public abstract class BaseFragment<V, T extends BasePresenter<V>> extends SupportFragment {
    protected T mPresenter;
    protected View mRootView;
    protected Toolbar mToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //建立与Presenter层的联系
        mPresenter = createPresenter();
        //Presenter层与View层建立联系
        mPresenter.attachView((V) this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null) {
            mRootView = inflater.inflate(createViewLayoutId(),container,false);
            mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        }
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar); //把Toolbar当做ActionBar给设置
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle("");
            if (canBack()) {
                actionBar.setDisplayHomeAsUpEnabled(true);//设置ActionBar一个返回箭头，主界面没有，次级界面有
            }
        }
        return mRootView;
    }

    /**
     * 初始化 Toolbar
     *
     * @param toolbar
     * @param homeAsUpEnabled
     * @param title
     */
    protected void initToolBar(Toolbar toolbar, boolean homeAsUpEnabled, String title) {
        ((BaseActivity)getActivity()).initToolBar(toolbar, homeAsUpEnabled, title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    /**
     * 判断当前 Fragment 是否允许返回
     * 主界面不允许返回，次级界面允许返回
     *
     * @return false
     */
    public boolean canBack() {
        return false;
    }

    protected abstract T createPresenter();

    protected abstract int createViewLayoutId();
}

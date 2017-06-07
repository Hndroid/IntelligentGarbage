package com.intelligentgarbage.intelligentgarbage.module.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Presenter基类
 * Created by null on 17-4-27.
 */

public abstract class BasePresenter<V> {

    protected Reference<V> mViewRef;

    public void attachView(V view){
        mViewRef = new WeakReference<V>(view);
    }

    protected V getView() {
        return mViewRef.get();
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }


    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }
}

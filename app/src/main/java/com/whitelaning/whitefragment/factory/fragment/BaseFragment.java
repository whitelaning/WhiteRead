package com.whitelaning.whitefragment.factory.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.whitelaning.whitefragment.factory.utils.RxBusUtils;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class BaseFragment extends RxFragment {

    protected Context mContext;
    protected View mRootView;

    private boolean isVisible;
    private boolean isPrepared;
    private boolean isFirstLoad = true;

    public boolean thisFragmentIsVisible() {
        return isVisible;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutId(), container, false);
        isFirstLoad = true;
        isPrepared = true;
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        afterViewCreated(savedInstanceState);
        toLazyLoad();
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        toLazyLoad();
    }

    protected void onInvisible() {
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected void toLazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
        lazyLoad();
    }

    protected abstract int getLayoutId();

    protected abstract void afterViewCreated(Bundle savedInstanceState);

    protected abstract void lazyLoad();//当View第一次可见的时候调用

    protected void RxBusCall(Bundle bundle) {

    }

    private Subscription rxsub;

    protected void openRxBus() {
        if (rxsub == null) {
            rxsub = RxBusUtils.getInstance().toObserverable()
                    .map(new Func1<Object, Bundle>() {
                        @Override
                        public Bundle call(Object o) {
                            return (Bundle) o;
                        }
                    })
                    .subscribe(new Action1<Bundle>() {
                        @Override
                        public void call(Bundle bundle) {
                            RxBusCall(bundle);
                        }
                    });
        }
    }

    protected void closeRxBus() {
        if (rxsub != null) {
            rxsub.unsubscribe();
        }
    }
}

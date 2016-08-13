package com.whitelaning.whitefragment.workspace.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.FragmentEvent;
import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.fragment.BaseFragment;
import com.whitelaning.whitefragment.workspace.main.network.entity.BaseSubscriber;
import com.whitelaning.whitefragment.factory.other.glide.GlideCircleTransform;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseRecyclerViewAdapter;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseViewHolder;
import com.whitelaning.whitefragment.factory.other.recyclerView.OnItemClickListener;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.ProgressStyle;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.XRecyclerView;
import com.whitelaning.whitefragment.workspace.main.activity.WebViewActivity;
import com.whitelaning.whitefragment.workspace.main.network.Network;
import com.whitelaning.whitefragment.workspace.main.network.entity.BaseResultEntity;
import com.whitelaning.whitefragment.workspace.main.network.entity.U148HomeModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Whitelaning on 2016/8/3.
 * Email：whitelaning@qq.com
 */
public class U148Fragment extends BaseFragment {


    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;

    private int PAGE_COUNT = 12;
    private int PAGE = 1;

    private List<U148HomeModel.DataBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public U148Fragment() {
        // Required empty public constructor
    }

    public static U148Fragment newInstance(int type) {
        U148Fragment fragment = new U148Fragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_u148;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 2) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            PAGE = 1;
        } else {
            PAGE = (int) Math.ceil((double) mListData.size() * 1.0 / PAGE_COUNT) + 1;
        }

        Network.getInstance().getServiceU148API()
                .getHomeData(PAGE)
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseResultEntity<U148HomeModel>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<BaseResultEntity<U148HomeModel>, U148HomeModel>() {
                    @Override
                    public U148HomeModel call(BaseResultEntity<U148HomeModel> u148HomeModelBaseResultEntity) {
                        return u148HomeModelBaseResultEntity.data;
                    }
                })
                .subscribe(new BaseSubscriber<U148HomeModel>() {
                    @Override
                    public void onNext(U148HomeModel response) {
                        super.onNext(response);
                        setListData(response, isClear);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        if (isClear) {
                            mRecyclerView.refreshComplete();
                        } else {
                            mRecyclerView.loadMoreComplete();
                        }
                    }
                });
    }

    private void setListData(U148HomeModel model, boolean isClear) {
        if (isClear) {
            mListData.clear();
        }

        mListData.addAll(model.getData());
        mRefreshAdapter.notifyDataSetChanged();

        if (isClear) {
            mRecyclerView.refreshComplete();
        } else {
            mRecyclerView.loadMoreComplete();
        }

        PAGE = model.getPageNo();
        PAGE_COUNT = model.getPageSize();
    }


    private void initViewStatus() {
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }

            @Override
            public void onLoadMore() {
                getData(false);
            }
        });

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_u148_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<U148HomeModel.DataBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, U148HomeModel.DataBean bean, int position) {
                getDetailData(bean);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, U148HomeModel.DataBean bean, int position) {
                return false;
            }
        });
    }

    private void getDetailData(final U148HomeModel.DataBean bean) {
        Network.getInstance().getServiceU148API()
                .getArticleData(bean.getId())
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseResultEntity<U148HomeModel>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<BaseResultEntity<U148HomeModel>, U148HomeModel>() {
                    @Override
                    public U148HomeModel call(BaseResultEntity<U148HomeModel> u148HomeModelBaseResultEntity) {
                        return u148HomeModelBaseResultEntity.data;
                    }
                })
                .subscribe(new BaseSubscriber<U148HomeModel>() {
                    @Override
                    public void onNext(U148HomeModel response) {
                        super.onNext(response);
                        WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, String.format("http://www.u148.net/article/%s.html", response.getId()));

                    }
                });
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<U148HomeModel.DataBean> {

        public RefreshAdapter(Context context, int layoutId, List<U148HomeModel.DataBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, U148HomeModel.DataBean bean) {
            holder.setText(R.id.mTitle, bean.getTitle());
            holder.setText(R.id.mContent, bean.getSummary());
            Glide.with(mContext)
                    .load(bean.getPic_mid())//目标URL
                    .placeholder(R.drawable.load_image_ing)
                    .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) holder.getView(R.id.mImage));
        }
    }
}


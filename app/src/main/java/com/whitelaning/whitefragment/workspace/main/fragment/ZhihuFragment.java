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
import com.whitelaning.whitefragment.factory.other.glide.GlideRoundTransform;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseRecyclerViewAdapter;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseViewHolder;
import com.whitelaning.whitefragment.factory.other.recyclerView.OnItemClickListener;
import com.whitelaning.whitefragment.factory.utils.UtilsTime;
import com.whitelaning.whitefragment.factory.utils.UtilsViewEvent;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.ProgressStyle;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.XRecyclerView;
import com.whitelaning.whitefragment.workspace.main.activity.WebViewActivity;
import com.whitelaning.whitefragment.workspace.main.network.Network;
import com.whitelaning.whitefragment.workspace.main.network.entity.ZhihuModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Whitelaning on 2016/8/9.
 * Email：whitelaning@qq.com
 */
public class ZhihuFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;

    private List<ZhihuModel.StoriesBean> mZhihuModels = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public ZhihuFragment() {
        // Required empty public constructor
    }

    public static ZhihuFragment newInstance(int type) {
        ZhihuFragment fragment = new ZhihuFragment();
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
        return R.layout.fragment_zhihu;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 3) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            toLoadFirstData(true);
        } else {
            toLoadMoreData(false);
        }
    }

    private SimpleDateFormat mSDF = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    private Calendar minCal;
    private Calendar lastCal;

    private void initLoadMoreData() {
        //知乎日报诞生日
        minCal = Calendar.getInstance();
        minCal.setTime(UtilsTime.stringTimeToDate("20130520", mSDF));

        lastCal = Calendar.getInstance();
        lastCal.setTime(new Date());
    }

    private void toLoadMoreData(final boolean isClear) {

        if(minCal == null) {
            initLoadMoreData();
        }

        lastCal.add(Calendar.DATE, -1);

        if (minCal.after(lastCal)) {
            mRecyclerView.setNoMore(true);
            return;
        }

        String id = UtilsTime.dateToStringTime(lastCal.getTime(), mSDF);

        Network.getInstance().getServiceZhihuAPI()
                .getBeforeData(id)
                .compose(this.<ZhihuModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ZhihuModel, List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public List<ZhihuModel.StoriesBean> call(ZhihuModel zhihuModel) {
                        return zhihuModel.stories;
                    }
                })
                .subscribe(new BaseSubscriber<List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public void onNext(List<ZhihuModel.StoriesBean> list) {
                        super.onNext(list);
                        setListData(list, isClear);
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

    private void toLoadFirstData(final boolean isClear) {
        Network.getInstance().getServiceZhihuAPI()
                .getLatestData()
                .compose(this.<ZhihuModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ZhihuModel, List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public List<ZhihuModel.StoriesBean> call(ZhihuModel zhihuModel) {
                        return zhihuModel.stories;
                    }
                })
                .subscribe(new BaseSubscriber<List<ZhihuModel.StoriesBean>>() {
                    @Override
                    public void onNext(List<ZhihuModel.StoriesBean> list) {
                        super.onNext(list);
                        setListData(list, isClear);
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

    private void setListData(List<ZhihuModel.StoriesBean> list, boolean isClear) {
        if (isClear) {
            initLoadMoreData();
            mZhihuModels.clear();
        }

        mZhihuModels.addAll(list);
        mRefreshAdapter.notifyDataSetChanged();

        if (isClear) {
            mRecyclerView.refreshComplete();
        } else {
            if (list.size() == 0) {
                mRecyclerView.setNoMore(true);
            } else {
                mRecyclerView.loadMoreComplete();
            }
        }
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

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_zhihu_fragment, mZhihuModels);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<ZhihuModel.StoriesBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, ZhihuModel.StoriesBean bean, int position) {
                if (!UtilsViewEvent.isFastDoubleClick()) {
                    WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, "http://daily.zhihu.com/story/" + bean.id);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, ZhihuModel.StoriesBean bean, int position) {
                return false;
            }
        });
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<ZhihuModel.StoriesBean> {

        public RefreshAdapter(Context context, int layoutId, List<ZhihuModel.StoriesBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, ZhihuModel.StoriesBean bean) {
            holder.setText(R.id.mTitle, bean.title);
            Glide.with(mContext)
                    .load(bean.images.get(0))//目标URL
                    .placeholder(R.drawable.load_image_ing)
                    .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                    .transform(new GlideRoundTransform(mContext))
                    .into((ImageView) holder.getView(R.id.mImage));
        }
    }
}

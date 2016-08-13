package com.whitelaning.whitefragment.workspace.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle.FragmentEvent;
import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.fragment.BaseFragment;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseRecyclerViewAdapter;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseViewHolder;
import com.whitelaning.whitefragment.factory.other.recyclerView.OnItemClickListener;
import com.whitelaning.whitefragment.factory.utils.UtilsDynamicSize;
import com.whitelaning.whitefragment.thirdparty.convenientbanner.ConvenientBanner;
import com.whitelaning.whitefragment.thirdparty.convenientbanner.holder.CBViewHolderCreator;
import com.whitelaning.whitefragment.thirdparty.convenientbanner.holder.Holder;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.ProgressStyle;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.XRecyclerView;
import com.whitelaning.whitefragment.workspace.main.activity.WebViewActivity;
import com.whitelaning.whitefragment.workspace.main.network.Network;
import com.whitelaning.whitefragment.workspace.main.network.entity.BaseSubscriber;
import com.whitelaning.whitefragment.workspace.main.network.entity.GankioHistoryModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zack White on 2016/7/31.
 */
public class GankioFragment extends BaseFragment {


    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private ConvenientBanner mConvenientBanner;

    private int type;

    private int PAGE_COUNT = 10;
    private int PAGE;

    private List<GankioHistoryModel.ResultsBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;
    private ArrayList<GankioHistoryModel.ResultsBean> mImageData = new ArrayList<>();

    public GankioFragment() {
        // Required empty public constructor
    }

    public static GankioFragment newInstance(int type) {
        GankioFragment fragment = new GankioFragment();
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
        return R.layout.fragment_gankio;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 1) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            PAGE = 1;
        } else {
            PAGE = (int) Math.ceil((double) mListData.size() * 1.0 / PAGE_COUNT) + 1;
        }

        Network.getInstance().getServiceGankioAPI()
                .getHistoryContent(PAGE_COUNT, PAGE)
                .compose(this.<GankioHistoryModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<GankioHistoryModel, List<GankioHistoryModel.ResultsBean>>() {
                    @Override
                    public List<GankioHistoryModel.ResultsBean> call(GankioHistoryModel gankioHistoryModel) {
                        return gankioHistoryModel.results;
                    }
                })
                .subscribe(new BaseSubscriber<List<GankioHistoryModel.ResultsBean>>() {
                    @Override
                    public void onNext(List<GankioHistoryModel.ResultsBean> list) {
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

    private void getVideoData() {
        Network.getInstance().getServiceGankioAPI()
                .getVideoList(5, 1)
                .compose(this.<GankioHistoryModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<GankioHistoryModel, List<GankioHistoryModel.ResultsBean>>() {
                    @Override
                    public List<GankioHistoryModel.ResultsBean> call(GankioHistoryModel gankioHistoryModel) {
                        return gankioHistoryModel.results;
                    }
                })
                .subscribe(new BaseSubscriber<List<GankioHistoryModel.ResultsBean>>() {
                    @Override
                    public void onNext(List<GankioHistoryModel.ResultsBean> list) {
                        super.onNext(list);
                        setHeardImage(list);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mRecyclerView.refreshComplete();
                    }
                });
    }

    private void setListData(List<GankioHistoryModel.ResultsBean> list, boolean isClear) {
        if (isClear) {
            mListData.clear();
        }

        mListData.addAll(list);
        mRefreshAdapter.notifyDataSetChanged();

        if (isClear) {
            mRecyclerView.refreshComplete();
        } else {
            mRecyclerView.loadMoreComplete();
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
                getVideoData();
                getData(true);
            }

            @Override
            public void onLoadMore() {
                getData(false);
            }
        });

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_gankio_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<GankioHistoryModel.ResultsBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, GankioHistoryModel.ResultsBean bean, int position) {
                WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, mListData.get(position).url);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, GankioHistoryModel.ResultsBean bean, int position) {
                return false;
            }
        });
    }

    public class LocalImageHolderView implements Holder<GankioHistoryModel.ResultsBean> {
        private ImageView mImage;
        private TextView mText;
        private TextView mType;

        @Override
        public View createView(Context context) {

            View item = LayoutInflater.from(mContext).inflate(R.layout.gankio_recyclerview_header_item, null, false);

            mImage = (ImageView) item.findViewById(R.id.mImage);
            mText = (TextView) item.findViewById(R.id.mText);
            mType = (TextView) item.findViewById(R.id.mType);

            return item;
        }

        @Override
        public void UpdateUI(Context context, int position, GankioHistoryModel.ResultsBean data) {
            Glide.with(mContext)
                    .load(data.randomImageUrl)//目标URL
                    .placeholder(R.drawable.load_image_ing)
                    .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存全尺寸图片，也缓存其他尺寸图片
                    .centerCrop()
                    .crossFade().into(mImage);

            mText.setText(data.desc);
            mType.setText(String.format("type:%s\nwho:%s", data.type, data.who));
        }
    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        if (mConvenientBanner != null) {
            //开始自动翻页
            mConvenientBanner.startTurning(5000);
        }

        openRxBus();
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        if (mConvenientBanner != null) {
            //停止翻页
            mConvenientBanner.stopTurning();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        closeRxBus();
    }

    @Override
    protected void RxBusCall(Bundle bundle) {
        super.RxBusCall(bundle);
        if (thisFragmentIsVisible() && "mTitle".equals(bundle.getString("click"))) {
            if (mRecyclerView != null) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private View header;

    private void setHeardImage(List<GankioHistoryModel.ResultsBean> list) {

        for (int i = 0; i < list.size(); i++) {
            GankioHistoryModel.ResultsBean bean = list.get(i);
            bean.randomImageUrl = "https://unsplash.it/960/480/?random&time=" + System.currentTimeMillis() + i;
        }

        if (mImageData.size() > 0 && mRecyclerView != null && mRecyclerView.hasThisHeaderView(header)) {
            mImageData.clear();
            mImageData.addAll(list);
            mConvenientBanner.notifyDataSetChanged();
        } else {
            //-----------------------
            header = LayoutInflater.from(mContext).inflate(R.layout.gankio_recyclerview_header, null, false);
            mConvenientBanner = (ConvenientBanner) header.findViewById(R.id.mConvenientBanner);
            UtilsDynamicSize.adaptiveImageByScreenWidth(mContext, mConvenientBanner, 960, 480);
            mRecyclerView.addHeaderView(header);
            mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());
            mRefreshAdapter.notifyDataSetChanged();
            //-----------------------
            mImageData.clear();
            mImageData.addAll(list);
            //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
            mConvenientBanner.setPages(
                    new CBViewHolderCreator<LocalImageHolderView>() {
                        @Override
                        public LocalImageHolderView createHolder() {
                            return new LocalImageHolderView();
                        }
                    }, mImageData)
                    //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                    //设置指示器的方向
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
            //        convenientBanner.setManualPageable(false);//设置不能手动影响
            mConvenientBanner.setOnItemClickListener(new com.whitelaning.whitefragment.thirdparty.convenientbanner.listener.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    WebViewActivity.toStartActivityForResult((Activity) mContext, 1000, mImageData.get(position).url);
                }
            });

            mConvenientBanner.startTurning(5000);
        }
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<GankioHistoryModel.ResultsBean> {

        public RefreshAdapter(Context context, int layoutId, List<GankioHistoryModel.ResultsBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(BaseViewHolder holder, GankioHistoryModel.ResultsBean bean) {
            holder.setText(R.id.mTitle, bean.desc);
            holder.setText(R.id.mPublishedAt, bean.publishedAt);
        }
    }
}

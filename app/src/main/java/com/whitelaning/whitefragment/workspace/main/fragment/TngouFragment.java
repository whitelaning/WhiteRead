package com.whitelaning.whitefragment.workspace.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.trello.rxlifecycle.FragmentEvent;
import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.fragment.BaseFragment;
import com.whitelaning.whitefragment.workspace.main.network.entity.BaseSubscriber;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseRecyclerViewAdapter;
import com.whitelaning.whitefragment.factory.other.recyclerView.BaseViewHolder;
import com.whitelaning.whitefragment.factory.other.recyclerView.OnItemClickListener;
import com.whitelaning.whitefragment.factory.other.recyclerView.itemDecoration.SpacesItemDecoration;
import com.whitelaning.whitefragment.factory.utils.UtilsDynamicSize;
import com.whitelaning.whitefragment.factory.utils.UtilsToast;
import com.whitelaning.whitefragment.factory.utils.UtilsViewEvent;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.ProgressStyle;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.XRecyclerView;
import com.whitelaning.whitefragment.workspace.main.activity.PhotoViewActivity;
import com.whitelaning.whitefragment.workspace.main.network.Network;
import com.whitelaning.whitefragment.workspace.main.network.entity.TngouModel;
import com.whitelaning.whitefragment.workspace.main.network.service.RequestServiceTngou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Zack White on 2016/8/3.
 */
public class TngouFragment extends BaseFragment {


    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;

    private int type;

    private int PAGE_COUNT = 20;
    private int PAGE = 1;
    private int ID = 1;

    private List<TngouModel.TngouBean> mListData = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;

    public TngouFragment() {
        // Required empty public constructor
    }

    public static TngouFragment newInstance(int type) {
        TngouFragment fragment = new TngouFragment();
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
        return R.layout.fragment_tngou;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 4) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            PAGE = 1;
            ID = (ID + 1) % 7;
        } else {
            PAGE = (int) Math.ceil((double) mListData.size() * 1.0 / PAGE_COUNT) + 1;
        }

        Network.getInstance().getServiceTngouAPI()
                .getListData(ID, PAGE, PAGE_COUNT)
                .compose(this.<TngouModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<TngouModel, List<TngouModel.TngouBean>>() {
                    @Override
                    public List<TngouModel.TngouBean> call(TngouModel model) {
                        return model.tngou;
                    }
                })
                .subscribe(new BaseSubscriber<List<TngouModel.TngouBean>>() {
                    @Override
                    public void onNext(List<TngouModel.TngouBean> list) {
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

    private void getDataDetail(int id) {
        Network.getInstance().getServiceTngouAPI()
                .getDataDetail(id)
                .compose(this.<TngouModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<TngouModel, List<TngouModel.Picture>>() {
                    @Override
                    public List<TngouModel.Picture> call(TngouModel model) {
                        return model.list;
                    }
                })
                .subscribe(new BaseSubscriber<List<TngouModel.Picture>>() {
                    @Override
                    public void onNext(List<TngouModel.Picture> list) {
                        super.onNext(list);

                        if(list.size() > 0) {
                            ArrayList<String> datas = new ArrayList<>();
                            for(TngouModel.Picture bean : list) {
                                datas.add(RequestServiceTngou.ImageHear + bean.src);
                            }
                            PhotoViewActivity.toStartActivityForResult((Activity) mContext,1000,datas,0);
                        } else{
                            UtilsToast.show(mContext,"No Picture");
                        }
                    }
                });
    }


    private void setListData(List<TngouModel.TngouBean> list, boolean isClear) {
        if (isClear) {
            mListData.clear();
        }

        mListData.addAll(list);
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

        StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        SpacesItemDecoration decoration = new SpacesItemDecoration(4);
        mRecyclerView.addItemDecoration(decoration);

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

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_tngou_fragment, mListData);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<TngouModel.TngouBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, TngouModel.TngouBean bean, int position) {
                if(!UtilsViewEvent.isFastDoubleClick()) {
                    getDataDetail(bean.id);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, TngouModel.TngouBean bean, int position) {
                return false;
            }
        });
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<TngouModel.TngouBean> {

        private Map<String, Integer> imageHeights = new HashMap<>();

        public RefreshAdapter(Context context, int layoutId, List<TngouModel.TngouBean> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(final BaseViewHolder holder, final TngouModel.TngouBean tngouBean, final int position) {
            super.convert(holder, tngouBean, position);

            final SimpleDraweeView mImage = holder.getView(R.id.mImage);
            final String url = RequestServiceTngou.ImageHear + tngouBean.img;

            Uri uri = Uri.parse(url);
            Postprocessor redMeshPostprocessor = new BasePostprocessor() {
                @Override
                public String getName() {
                    return url;
                }

                @Override
                public void process(Bitmap bitmap) {
                    if (imageHeights.get(url + "height") == null) {
                        imageHeights.put(url + "height", UtilsDynamicSize.defaultDisplayWidth / 3 * bitmap.getHeight() / bitmap.getWidth());
                        imageHeights.put(url + "width", UtilsDynamicSize.defaultDisplayWidth / 3);
                    }

                    mImage.getLayoutParams().height = imageHeights.get(url + "height");
                    mImage.getLayoutParams().width = imageHeights.get(url + "width");
                }
            };

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(redMeshPostprocessor)
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController)
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(mImage.getController())
                            // other setters as you need
                            .build();
            mImage.setController(controller);
        }

        @Override
        public void convert(BaseViewHolder holder, TngouModel.TngouBean bean) {

        }
    }
}
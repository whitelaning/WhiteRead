package com.whitelaning.whitefragment.workspace.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.whitelaning.whitefragment.factory.utils.UtilsViewEvent;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.ProgressStyle;
import com.whitelaning.whitefragment.thirdparty.xrecyclerview.XRecyclerView;
import com.whitelaning.whitefragment.workspace.main.activity.PhotoViewActivity;
import com.whitelaning.whitefragment.workspace.main.network.Network;
import com.whitelaning.whitefragment.workspace.main.network.entity.MzituModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

/**
 * Created by Zack White on 2016/8/7.
 */
public class MzituFragment extends BaseFragment {


    @BindView(R.id.mRecyclerView)
    XRecyclerView mRecyclerView;
    @BindView(R.id.mTagLayout)
    LinearLayout mTagLayout;

    private int type; //fragment 标示

    private int PAGE = 1;
    private int maxPageNumber = 1;
    private int maxImageNumber = 1;

    private int sourceType;//数据源标示，0表示首页，1表示tag页
    private String tagString;

    private ArrayList<MzituModel> mMzituManModelList = new ArrayList<>();
    private ArrayList<MzituModel> mMzituTempModelList = new ArrayList<>();
    private ArrayList<MzituModel> mMzituTagModelList = new ArrayList<>();
    private ArrayList<MzituModel> mMzituTopModelList = new ArrayList<>();

    private ArrayList<String> detialImages = new ArrayList<>();

    private RefreshAdapter mRefreshAdapter;

    private LinearLayout mTopLayout;
    private View topView;

    public MzituFragment() {
        // Required empty public constructor
    }

    public static MzituFragment newInstance(int type) {
        MzituFragment fragment = new MzituFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        initSomething();
    }

    private void initSomething() {
        PAGE = 1;
        maxPageNumber = 1;
        maxImageNumber = 1;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mzitu;
    }

    @Override
    protected void afterViewCreated(Bundle savedInstanceState) {
        initViewStatus();
    }

    @Override
    protected void lazyLoad() {
        if (type == 5) {
            mRecyclerView.setRefreshing(true);
        }
    }

    private void getData(final boolean isClear) {
        if (isClear) {
            if (sourceType == 0) {
                toLoadFirstData(true);
            } else {
                toLoadTagFirstData(true);
            }
        } else {
            if (sourceType == 0) {
                toLoadMoreData(false);
            } else {
                toLoadTagMoreData(false);
            }
        }
    }

    private void toLoadTagMoreData(final boolean isClear) {
        if (PAGE + 1 <= maxPageNumber) {
            PAGE += 1;
        } else {
            mMzituTempModelList.clear();

            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = isClear;
            mHandler.sendMessage(msg);

            return;
        }

        Network.getInstance().getServiceMzitu()
                .getTagMoreData(tagString, PAGE, System.currentTimeMillis())
                .compose(this.<String>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String html) {
                        super.onNext(html);
                        toParserHTML(html, isClear, true);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = isClear;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    private void toLoadTagFirstData(final boolean isClear) {
        Network.getInstance().getServiceMzitu()
                .getTagData(tagString, System.currentTimeMillis())
                .compose(this.<String>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String html) {
                        super.onNext(html);
                        toParserHTML(html, isClear, true);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = isClear;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    private void toLoadMoreData(final boolean isClear) {
        if (PAGE + 1 <= maxPageNumber) {
            PAGE += 1;
        } else {
            mMzituTempModelList.clear();

            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = isClear;
            mHandler.sendMessage(msg);

            return;
        }

        Network.getInstance().getServiceMzitu()
                .getIndexMoreData(PAGE, System.currentTimeMillis())
                .compose(this.<String>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String html) {
                        super.onNext(html);
                        toParserHTML(html, isClear);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = isClear;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    private void toLoadFirstData(final boolean isClear) {
        Network.getInstance().getServiceMzitu()
                .getIndexData(System.currentTimeMillis())
                .compose(this.<String>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String html) {
                        super.onNext(html);
                        toParserHTML(html, isClear);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = isClear;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        openRxBus();
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

    private void toParserDetailHTML(String html) {

        detialImages.clear();

        Document doc = Jsoup.parse(html);
        Elements mainElements = doc.getElementsByClass("main-image");
        Elements imgSrcs = mainElements.select("img[src]");
        String imagePath = imgSrcs.first().attr("src");
        String imageNumber = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));

        String baseUrl = imagePath.replace(imageNumber, "%s%s");

        String name1 = imageNumber.substring(0, 3);
        String name2 = imageNumber.substring(4);

        Elements pagenaviElements = doc.getElementsByClass("pagenavi");
        Elements ahrefs = pagenaviElements.select("a[href]");
        String maxPageHref = ahrefs.get(ahrefs.size() - 2).attr("href");
        String maxImageNumberString = maxPageHref.substring(maxPageHref.lastIndexOf("/") + 1);
        maxImageNumber = Integer.parseInt(maxImageNumberString);


        int currentNumber = Integer.parseInt(name2);

        while (currentNumber <= maxImageNumber) {
            String path;
            if (currentNumber < 10) {
                path = String.format(baseUrl, name1, "0" + currentNumber);
            } else {
                path = String.format(baseUrl, name1, currentNumber);
            }
            detialImages.add(path);

            currentNumber++;
        }

        mHandler.sendEmptyMessage(4);//显示详情页面
    }

    private void toParserHTML(String html, boolean isClear) {
        toParserHTML(html, isClear, false);
    }

    /**
     * 解析数据
     *
     * @param html        源码
     * @param isClear     是否清空
     * @param onlyContent 是否只解析图片内容
     */
    private void toParserHTML(String html, boolean isClear, boolean onlyContent) {
        Document doc = Jsoup.parse(html);

        //main
        //main max page
        Elements navigationPaginationClass = doc.getElementsByClass("nav-links");
        Elements media = navigationPaginationClass.first().select("a[href]");
        String maxPageHref = media.get(media.size() - 2).attr("href");
        String maxPageNumberString = maxPageHref.substring(maxPageHref.lastIndexOf("/") + 1, maxPageHref.indexOf("?"));
        maxPageNumber = Integer.parseInt(maxPageNumberString);

        //main
        //main post list
        mMzituTempModelList.clear();
        Elements postListClass = doc.getElementsByClass("postlist");
        Elements lis = postListClass.select("li");
        for (Element liItem : lis) {
            Element lia = liItem.select("a[href]").first();
            String href = lia.attr("href");

            Element liai = lia.select("img").first();
            String width = liai.attr("width");
            String height = liai.attr("height");
            String alt = liai.attr("alt");
            String src = liai.attr("src");
            String dataOriginal = liai.attr("data-original");

            MzituModel manModel = new MzituModel(href, alt, dataOriginal, width, height);
            mMzituTempModelList.add(manModel);
        }

        Message msg3 = Message.obtain();
        msg3.what = 3;
        Bundle mBundle3 = new Bundle();
        mBundle3.putBoolean("isClear", isClear);
        msg3.setData(mBundle3);
        mHandler.sendMessage(msg3);//设置List主要信息

        if (onlyContent) {
            return;
        }

        // hot top
        mMzituTopModelList.clear();
        Elements toplinksClass = doc.getElementsByClass("widgets_top");
        Elements toplinks = toplinksClass.select("a[href]"); //带有href属性的a元素
        for (Element linkItem : toplinks) {
            String href = linkItem.attr("href");

            Elements imgElements = linkItem.select("img[src]");
            Element imgElement1 = imgElements.first();

            String alt = imgElement1.attr("alt");
            String src = imgElement1.attr("src");
            String width = imgElement1.attr("width");
            String height = imgElement1.attr("height");

            MzituModel tagModel = new MzituModel(href, alt, src, width, height);
            mMzituTopModelList.add(tagModel);
        }

        Message msg1 = Message.obtain();
        msg1.what = 1;
        Bundle mBundle1 = new Bundle();
        msg1.setData(mBundle1);
        mHandler.sendMessage(msg1);//设置List头部控件信息

        // Tag
        mMzituTagModelList.clear();
        mMzituTagModelList.add(new MzituModel("www.mzitu.com", "全部"));
        Elements taglinksClass = doc.getElementsByClass("widgets_hot");
        Elements taglinks = taglinksClass.select("a[href]"); //带有href属性的a元素
        for (Element linkItem : taglinks) {
            String tagLink = linkItem.attr("href");
            String tagText = linkItem.text();
            MzituModel tagModel = new MzituModel(tagLink, tagText);
            mMzituTagModelList.add(tagModel);
        }

        Message msg2 = Message.obtain();
        msg2.what = 2;
        Bundle mBundle2 = new Bundle();
        msg2.setData(mBundle2);
        mHandler.sendMessage(msg2);//设置Tag信息
    }

    private void setTopLayoutData() {
        if (mMzituTopModelList == null || mMzituTopModelList.size() < 4 || topView != null) {
            return;
        }

        topView = LayoutInflater.from(mContext).inflate(R.layout.mzitu_header_top, null, false);
        mTopLayout = (LinearLayout) topView.findViewById(R.id.mTopLayout);
        for (final MzituModel item : mMzituTopModelList) {
            ImageView mImage = new ImageView(mContext);
            mImage.setLayoutParams(new LinearLayoutCompat.LayoutParams(UtilsDynamicSize.defaultDisplayWidth / 4, UtilsDynamicSize.defaultDisplayWidth / 4));
            mImage.setPadding(4, 4, 4, 0);
            Glide.with(mContext)
                    .load(item.imagePath)//目标URL
                    .placeholder(R.drawable.load_image_ing)
                    .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存全尺寸图片，也缓存其他尺寸图片
                    .centerCrop()
                    .crossFade().into(mImage);

            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!UtilsViewEvent.isFastDoubleClick()) {
                        getDetailData(item.link);
                    }
                }
            });

            mTopLayout.addView(mImage);
        }

        mRecyclerView.addHeaderView(topView);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());
    }

    private void setTagLayoutData() {
        if (mMzituTagModelList != null && mMzituTagModelList.size() > 0 && mTagLayout.getChildCount() == 0) {
            for (final MzituModel item : mMzituTagModelList) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.mzitu_header_tag, mTagLayout, false);
                TextView mTitle = (TextView) view.findViewById(R.id.mTitle);
                mTitle.setText(item.text);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!UtilsViewEvent.isFastDoubleClick()) {
                            toChangeFlowChildViewStatus(view);
                            if(item.text.equals("全部")) {
                                sourceType = 0;
                            } else {
                                sourceType = 1;
                                tagString = item.link.substring(item.link.lastIndexOf("/") + 1);
                                initSomething();//重新初始化一些东西
                            }
                            mRecyclerView.setRefreshing(true);
                        }
                    }
                });

                mTagLayout.addView(view);
            }
        }
    }

    private void toChangeFlowChildViewStatus(View view1) {
        int count = mTagLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view2 = mTagLayout.getChildAt(i);
            if (view2.equals(view1)) {
                TextView mTitle = (TextView) view2.findViewById(R.id.mTitle);
                mTitle.setBackgroundResource(R.drawable.shape_2);
                mTitle.setTextColor(0xFFFF0000);
            } else {
                TextView mTitle = (TextView) view2.findViewById(R.id.mTitle);
                mTitle.setBackgroundResource(R.drawable.shape_1);
                mTitle.setTextColor(0xFF6c6c6c);
            }
        }
    }

    private void setListData(ArrayList<MzituModel> list, boolean isClear) {
        if (isClear) {
            mMzituManModelList.clear();
        }

        mMzituManModelList.addAll(list);
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

        mRefreshAdapter = new RefreshAdapter(mContext, R.layout.item_list_mzitu_fragment, mMzituManModelList);
        mRecyclerView.setAdapter(mRefreshAdapter);
        mRefreshAdapter.setHeaderViewsNumber(mRecyclerView.getTopViewCount());

        mRefreshAdapter.setOnItemClickListener(new OnItemClickListener<MzituModel>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, MzituModel bean, int position) {
                if (!UtilsViewEvent.isFastDoubleClick()) {
                    getDetailData(bean.link);
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, MzituModel bean, int position) {
                return false;
            }
        });
    }

    private void getDetailData(String url) {
        Network.getInstance().getServiceMzitu()
                .getDetialData(url.substring(url.lastIndexOf("/") + 1), System.currentTimeMillis())
                .compose(this.<String>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String html) {
                        super.onNext(html);
                        toParserDetailHTML(html);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private class RefreshAdapter extends BaseRecyclerViewAdapter<MzituModel> {

        private Map<String, Integer> imageHeights = new HashMap<>();

        public RefreshAdapter(Context context, int layoutId, List<MzituModel> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(final BaseViewHolder holder, final MzituModel bean, final int position) {
            super.convert(holder, bean, position);

            final SimpleDraweeView mImage = holder.getView(R.id.mImage);
            final String url = bean.imagePath;

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
        public void convert(BaseViewHolder holder, MzituModel bean) {

        }
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MzituFragment> mInstance;

        MyHandler(MzituFragment instance) {
            mInstance = new WeakReference<>(instance);
        }

        @Override
        public void handleMessage(Message msg) {

            MzituFragment fragment = mInstance.get();

            switch (msg.what) {
                case 0://通知获取数据完毕，刷新控件状态
                    if ((boolean) msg.obj) {
                        fragment.mRecyclerView.refreshComplete();
                    } else {
                        fragment.mRecyclerView.loadMoreComplete();
                    }
                    break;
                case 1://设置头部信息\
                    fragment.setTopLayoutData();
                    break;
                case 2://设置Tag信息
                    fragment.setTagLayoutData();
                    break;
                case 3://设置Man信息
                    fragment.setListData(fragment.mMzituTempModelList, msg.getData().getBoolean("isClear"));
                    break;
                case 4://显示详情
                    PhotoViewActivity.toStartActivityForResult((Activity) fragment.mContext, 1000, fragment.detialImages, 0);
                    break;
            }
        }
    }
}

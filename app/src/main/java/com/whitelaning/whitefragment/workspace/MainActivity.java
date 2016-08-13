package com.whitelaning.whitefragment.workspace;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.activity.BaseActivity;
import com.whitelaning.whitefragment.factory.console.ActivityCollector;
import com.whitelaning.whitefragment.factory.utils.RxBusUtils;
import com.whitelaning.whitefragment.factory.utils.UtilsToast;
import com.whitelaning.whitefragment.factory.utils.UtilsViewEvent;
import com.whitelaning.whitefragment.thirdparty.magicindicator.MagicIndicator;
import com.whitelaning.whitefragment.thirdparty.magicindicator.buildins.commonnavigator.CommonNavigator;
import com.whitelaning.whitefragment.thirdparty.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import com.whitelaning.whitefragment.thirdparty.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import com.whitelaning.whitefragment.thirdparty.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import com.whitelaning.whitefragment.thirdparty.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import com.whitelaning.whitefragment.thirdparty.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import com.whitelaning.whitefragment.workspace.main.fragment.GankioFragment;
import com.whitelaning.whitefragment.workspace.main.fragment.MzituFragment;
import com.whitelaning.whitefragment.workspace.main.fragment.TngouFragment;
import com.whitelaning.whitefragment.workspace.main.fragment.U148Fragment;
import com.whitelaning.whitefragment.workspace.main.fragment.ZhihuFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.mBack)
    TextView mBack;
    @BindView(R.id.mTitle)
    TextView mTitle;
    @BindView(R.id.mMenu)
    TextView mMenu;
    @BindView(R.id.mActionBarBackground)
    RelativeLayout mActionBarBackground;
    @BindView(R.id.mMagicIndicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;


    private FragAdapter mAdapter;

    private List<String> mDataList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initViewStatus();
        initData();
        initViewPager();
        initIndicator();
    }

    private void initData() {
        mDataList.add("Gank.io");
        mDataList.add("U148.net");
        mDataList.add("Zhihu.com");
        mDataList.add("Tngou.net");
        mDataList.add("Mzitu.com");
        //构造适配器
        fragments.add(GankioFragment.newInstance(1));
        fragments.add(U148Fragment.newInstance(2));
        fragments.add(ZhihuFragment.newInstance(3));
        fragments.add(TngouFragment.newInstance(4));
        fragments.add(MzituFragment.newInstance(5));


        mAdapter = new FragAdapter(getSupportFragmentManager(), fragments);
    }

    private void initViewStatus() {
        mTitle.setText("白色阅读");
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mMagicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                mMagicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mMagicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    private void initIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setEnablePivotScroll(true); //当前页始终定位到中间
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getItemView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#727272"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#eeeeee"));

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;

            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#2A2A2A"));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
    }

    @OnClick({R.id.mBack, R.id.mTitle, R.id.mMenu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBack:
                break;
            case R.id.mTitle:
                Bundle bundle = new Bundle();
                bundle.putString("click", "mTitle");
                RxBusUtils.getInstance().send(bundle);
                break;
            case R.id.mMenu:
                break;
        }
    }

    public class FragAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(!UtilsViewEvent.isFastDoubleClick2()) {
                UtilsToast.show("Fast double click to exit");
                return true;
            } else {
                ActivityCollector.finishAllActivity();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

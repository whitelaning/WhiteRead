package com.whitelaning.whitefragment.workspace.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.activity.BaseActivity;
import com.whitelaning.whitefragment.factory.utils.UtilsImage;
import com.whitelaning.whitefragment.factory.utils.UtilsToast;
import com.whitelaning.whitefragment.factory.utils.UtilsViewEvent;
import com.whitelaning.whitefragment.thirdparty.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoViewActivity extends BaseActivity {

    @BindView(R.id.mBack)
    TextView mBack;
    @BindView(R.id.mTitle)
    TextView mTitle;
    @BindView(R.id.mMenu)
    TextView mMenu;
    @BindView(R.id.mLine)
    View mLine;

    @BindView(R.id.mActionBarBackground)
    RelativeLayout mActionBarBackground;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    public final static String KEY_LIST = "PhotoViewActivityList";
    public final static String KEY_POSITION = "PhotoViewActivityPosition";

    private ArrayList<String> list;
    private int position;

    public static void toStartActivityForResult(Activity mActivity, int requestCode, ArrayList<String> list, int position) {
        Intent intent = new Intent(mActivity, PhotoViewActivity.class);
        intent.putExtra(KEY_LIST, list);
        intent.putExtra(KEY_POSITION, position);
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);

        initIntent();
        initActionBar();
        initViewPager();
    }

    private void initActionBar() {

        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mMenu.setText("保存");
        mMenu.setVisibility(View.VISIBLE);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UtilsViewEvent.isFastDoubleClick()) {
                    saveImgToSD();
                }
            }
        });

        mTitle.setText(String.format(Locale.CHINA, "%d/%d", position + 1, list.size()));
    }

    private void initViewPager() {
        mViewPager.setCurrentItem(position);
        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mViewPager.setAdapter(new PagerAdapter() {

            private Map<String, Boolean> mapImageStatus = new HashMap<String, Boolean>();

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView view = new PhotoView(PhotoViewActivity.this);
                view.enable();
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);

                String url = list.get(position);
                mapImageStatus.put(url, true);

                loadImageTalk(view, url);

                setListener(view, url);

                container.addView(view);
                return view;
            }

            private void setListener(PhotoView view, final String url) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!UtilsViewEvent.isFastDoubleClick() && mapImageStatus.get(url) != null && !mapImageStatus.get(url)) {
                            loadImageTalk((PhotoView) view, url);
                        }
                    }
                });
            }

            private void loadImageTalk(PhotoView view, final String url) {
                Glide.with(PhotoViewActivity.this)
                        .load(url)//目标URL
                        .placeholder(R.drawable.load_image_ing)
                        .error(R.drawable.load_image_fail) //图片获取失败时默认显示的图片
                        .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存全尺寸图片，也缓存其他尺寸图片
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                mapImageStatus.put(url, false);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mapImageStatus.put(url, true);
                                return false;
                            }
                        })
                        .crossFade().into(view);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int index) {
                position = index;
                mTitle.setText(String.format(Locale.CHINA, "%d/%d", position + 1, list.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(KEY_LIST)) {
            finish();
            return;
        }

        list = intent.getStringArrayListExtra(KEY_LIST);
        position = intent.getIntExtra(KEY_POSITION, 0);
    }

    public void saveImgToSD() {
        String url;
        if (null != list && list.size() > 0) {
            url = list.get(position);
        } else {
            UtilsToast.show("Save fail");
            return;
        }

        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        UtilsToast.show("Save fail");
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        try {
                            String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "WhiteRead";
                            String fileName = System.currentTimeMillis() + ".jpg";

                            String path = UtilsImage.savePhotoToSDCard(fileDir, fileName, resource);

                            File file = new File(path);

                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(file));
                            sendBroadcast(intent);

                            UtilsToast.show("Saved");
                        } catch (Exception e) {
                            UtilsToast.show("Save fail");
                            e.printStackTrace();
                        }
                    }
                });
    }
}

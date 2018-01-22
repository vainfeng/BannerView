package com.view.vain.bannerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vain on 2017/5/5.
 */
public class BannerView extends FrameLayout {
    private static final String TAG = BannerView.class.getSimpleName();


    public static final int DEFAULT_DELAY = 1000; // 默认延迟 1S
    public static final int DEFAULT_PERIOD = 5000; // 默认轮播间隔 5S
    private static final int DEFAULT_INDICATOR_SIZE = 5; // 指示器大小和间距，默认5dp

    private ViewPager bannerViewPager;
    private BannerAdapter adapter;
    private RadioGroup indicatorGroup;
    private Drawable defaultBannerPic; // banner没有加载完成之前默认的占位图
    private OnBannerClickListener clickListener;
    private List<String> originBannerUrls = new ArrayList<>();
    private int indicatorDefaultSize;
    private int indicatorWidth;
    private int indicatorHeight;
    private int indicatorSelectorId;

    private long delay;
    private long period;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            bannerViewPager.setCurrentItem(bannerViewPager.getCurrentItem() + 1);
            bannerViewPager.postDelayed(task, period);
        }
    };

    private boolean isRunning = false;


    public void setOnBannerClickListener(OnBannerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadAttr(context, attrs);
        init();
    }

    public BannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttr(context, attrs);
        init();
    }

    @TargetApi(21)
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAttr(context, attrs);
        init();
    }


    /**
     * 开启自动滚动
     */
    public void startAuto() {
        startAuto(delay, period);
    }

    public void startAuto(long delay, long period) {
        if (isRunning) return;

        isRunning = true;
        this.delay = delay;
        this.period = period;
        bannerViewPager.postDelayed(task, delay);
    }

    /**
     * 停止自动滚动
     */
    public void stopAuto() {
        bannerViewPager.removeCallbacks(task);
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }


    /**
     * 设置占位Banner图
     * @param drawableId
     */
    public void setDefaultBanner(int drawableId) {
        defaultBannerPic = ContextCompat.getDrawable(getContext(),drawableId);
    }

    /**
     * 设置ViewPager的缓存个数
     *
     * @param limit
     */
    public void setOffsetLimit(int limit) {
        bannerViewPager.setOffscreenPageLimit(limit);
    }


    private void loadAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Banner);

        float scale = getResources().getDisplayMetrics().density;
        int defaultSize = (int) (DEFAULT_INDICATOR_SIZE * scale + 0.5F);
        indicatorDefaultSize = array.getDimensionPixelSize(R.styleable.Banner_indicatorSize, defaultSize);

        indicatorWidth = array.getDimensionPixelSize(R.styleable.Banner_indicatorWidth, defaultSize);
        indicatorHeight = array.getDimensionPixelSize(R.styleable.Banner_indicatorHeight, defaultSize);

        indicatorSelectorId = array.getResourceId(R.styleable.Banner_indicatorSelector, 0);
        defaultBannerPic = array.getDrawable(R.styleable.Banner_defaultPic);
        delay = array.getInteger(R.styleable.Banner_delay, DEFAULT_DELAY);
        period = array.getInteger(R.styleable.Banner_period, DEFAULT_PERIOD);

        array.recycle();
    }

    private void init() {
        inflate(getContext(), R.layout.view_banner, this);
    }


    public ViewPager getBannerViewPager() {
        return bannerViewPager;
    }

    /**
     * 是否显示页面指示器
     *
     * @param isShow
     */
    public void showIndicator(boolean isShow) {
        indicatorGroup.setVisibility(isShow ? VISIBLE : GONE);
    }

    private boolean isShowIndicators() {
        return indicatorGroup.getVisibility() == VISIBLE;
    }

    /**
     * 渲染完Xml后的回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        bannerViewPager = (ViewPager) findViewById(R.id.vpBanners);
        indicatorGroup = (RadioGroup) findViewById(R.id.rgIndicators);
        adapter = new BannerAdapter();

        bannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position %= originBannerUrls.size();
                if (position < 0) {
                    position = originBannerUrls.size() + position;
                }
                if (isShowIndicators()) {
                    RadioButton indicator = (RadioButton) indicatorGroup.getChildAt(position);
                    if (indicator != null) {
                        indicator.setChecked(true);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void loadBanners(List<String> originUrls) {
        if (originUrls == null || originUrls.size() <= 0) {
            originUrls = new ArrayList<>();
            originUrls.add("");
        }

        originBannerUrls = originUrls;

        if (bannerViewPager.getAdapter() == null) {
            bannerViewPager.setAdapter(adapter);
        }

        // 创建指示器
        indicatorGroup.removeAllViews();
        if (originUrls.size() > 1) {
            for (int i = 0; i < originUrls.size(); i++) {
                indicatorGroup.addView(createIndicator());
            }
            ((RadioButton) indicatorGroup.getChildAt(bannerViewPager.getCurrentItem())).setChecked(true);
        }

        adapter.notifyDataSetChanged();


    }

    /**
     * 创建指示器View
     *
     * @return
     */
    private RadioButton createIndicator() {
        RadioButton indicator = new RadioButton(getContext());
        indicator.setButtonDrawable(android.R.color.transparent);
        indicator.setClickable(false);
        indicator.setBackgroundResource(indicatorSelectorId);

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                indicatorWidth > 0 ? indicatorWidth : indicatorDefaultSize,
                indicatorHeight > 0 ? indicatorHeight : indicatorDefaultSize);
        lp.setMargins(indicatorDefaultSize, 0, 0, 0);
        indicator.setLayoutParams(lp);

        return indicator;
    }


    private class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求模取出View列表中要显示的项
            position %= originBannerUrls.size();
            if (position < 0) {
                position = originBannerUrls.size() + position;
            }
            ImageView imageView = createBannerImage(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = imageView.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        /**
         * 创建banner视图
         *
         * @return
         */
        private ImageView createBannerImage(final int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getContext()).load(originBannerUrls.get(position)).placeholder(defaultBannerPic).error(defaultBannerPic).into(imageView);
            imageView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onBannerClick(position);
                }
            });

            return imageView;
        }

    }

}

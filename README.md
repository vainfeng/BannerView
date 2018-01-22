# BannerView  使用介绍


### 使用

在layout.xml中使用：

```
<com.view.vain.bannerview.BannerView
        android:id="@+id/bannerView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        banner:indicatorSize="5dp"
        banner:indicatorSelector="@drawable/selector_home_banner_indicator"
        />
```


在 Activity中调用`loadbanners(List<String> urls)`函数加载图片。这里用的是Glide作为图片加载框架。别忘了在Mainifest.xml中声明网络权限

```
<uses-permission android:name="android.permission.INTERNET" />
```

可以通过调用`bannerView.startAuto();`来启动自动轮播功能。它可以通过重载方法设置delay和period。或者通过自定义属性在xml中声明。


#### 支持如下styles属性设置

```
<declare-styleable name="Banner">
        <!-- 指示器SIZE，如果indicatorWidth和indicatorHeight都没有指定，则指示器长宽都取indicatorSize的值-->
        <attr name="indicatorSize" format="dimension" />
        <!--  指示器的宽度-->
        <attr name="indicatorWidth" format="dimension" />
        <!--  指示器的高度-->
        <attr name="indicatorHeight" format="dimension" />
        <!--  首次自动轮播的延迟 默认1S-->
        <attr name="delay" format="integer" />
        <!--  自动轮播的间隔周期  默认5S-->
        <attr name="period" format="integer" />
        <!--  指示器的样式选择器，这里支持selector-->
        <attr name="indicatorSelector" format="reference" />
        <!--  图片没有加载出来的时候显示的占位图-->
        <attr name="defaultPic" format="reference" />
</declare-styleable>
```
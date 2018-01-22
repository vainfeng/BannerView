package com.view.vain.bannerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private BannerView bannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerView = (BannerView) findViewById(R.id.bannerView);
        bannerView.setDefaultBanner(R.mipmap.loading);
        bannerView.loadBanners(generateBannerPic());
        bannerView.startAuto();

        Button switchBtn = (Button) findViewById(R.id.btnSwitch);
        switchBtn.setOnClickListener(v -> {
            if (bannerView.isRunning()) {
                bannerView.stopAuto();
            } else {
                bannerView.startAuto(1000, 2000);
            }
        });
    }

    private List<String> generateBannerPic() {
        List<String> urls = new ArrayList<>();
        urls.add("http://opo2omomn.bkt.clouddn.com/BingWallpaper-2016-11-10.jpg");
        urls.add("http://opo2omomn.bkt.clouddn.com/gamersky_04origin_07_201354148FE6.jpg");
        urls.add("http://opo2omomn.bkt.clouddn.com/gamersky_34origin_67_20151120175618D.jpg");
        return urls;
    }


}

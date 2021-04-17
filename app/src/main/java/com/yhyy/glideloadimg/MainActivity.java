package com.yhyy.glideloadimg;

import android.os.Bundle;
import android.widget.ImageView;

import com.yhyy.glideloadimg.config.AppConfig;
import com.yhyy.glideloadimg.utils.GlideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.iv_item1)
    ImageView ivItem1;
    @BindView(R.id.iv_item2)
    ImageView ivItem2;
    @BindView(R.id.iv_item3)
    ImageView ivItem3;
    @BindView(R.id.iv_item4)
    ImageView ivItem4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initToolBar();
        initListener();
    }

    private void initListener() {
        GlideUtil.GlideNI(context, AppConfig.img, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, ivItem1);
        GlideUtil.GlideLI(context, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, ivItem2);
        GlideUtil.GlideCI(context, AppConfig.img, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, ivItem3);
        GlideUtil.GlideBI(context, AppConfig.img, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, ivItem4);
    }

    private void initToolBar() {
    }

    private void initData() {

    }
}
package com.hx.steven.app;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hx.steven.BuildConfig;

/**
 * Created by user on 2018/1/15.
 */

public class MyApp extends BaseApplication {

    @Override
    public AppInitBuilder getAppInitBuilder() {
        return new AppInitBuilder.Builder()
                .setInitLogger(true)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        try {
//            RetrofitNetManager.getInstance().init("https://www.wanandroid.com");
//            RetrofitNetManager.getInstance().setApiService(RetrofitNetManager.getInstance().getRetrofit().create(ApiService.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}

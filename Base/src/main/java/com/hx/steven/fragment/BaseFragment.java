package com.hx.steven.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.hx.steven.R;
import com.hx.steven.activity.BaseActivity;
import com.hx.steven.activity.OnPermissionListener;
import com.hx.steven.component.MultipleStatusView;
import com.permissionx.guolindev.PermissionX;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 内容视图的params
     */
    LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
    /**
     * 上下文对象
     */
    public Context context;
    /**
     * 获取该fragment依附的activity
     */
    public Activity activity;
    /**
     * 容器视图
     */
    private ViewGroup mContainer;
    /**
     * 内容多状态视图
     */
    private MultipleStatusView multipleStatusView;
    /**
     * 是否显示MultipleView
     */
    private boolean enableMultiple;
    /**
     * 骨架屏
     */
    private SkeletonScreen skeletonScreen;
    /**
     * 是否使用骨架屏
     */
    private boolean enableSkeletonScreen;
    /**
     * 骨架屏view
     */
    private View skeletonView;
    /**
     * 骨架屏recycle view
     */
    private RecyclerView skeletonRecyclerview;
    /**
     * 骨架屏recycle adapter
     */
    private RecyclerView.Adapter skeletonAdapter;
    /**
     * 显示骨架
     */
    private int skeletonLayout;

    protected abstract int getContentId();

    protected abstract void initView(View view);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_fragmemt, container, false);
        mContainer = view.findViewById(R.id.base_container);
        /**初始化内容视图*/
        View layout = inflater.inflate(getContentId(), null);
        layout.setLayoutParams(contentParams);
        /**-----视图添加逻辑-----*/
        if (enableMultiple) {
            /**初始化multipleView*/
            multipleStatusView = new MultipleStatusView(context);
            multipleStatusView.setLayoutParams(contentParams);
            multipleStatusView.addView(layout);
            mContainer.addView(multipleStatusView);
        } else {
            mContainer.addView(layout);
        }
        initView(view);
        if (enableSkeletonScreen) this.initSkeleton();
        return view;
    }

    private void initSkeleton() {
        if (skeletonRecyclerview != null && skeletonAdapter != null) {
            skeletonScreen = Skeleton.bind(skeletonRecyclerview)
                    .adapter(skeletonAdapter)//设置实际adapter
                    .shimmer(true)//是否开启动画
                    .angle(30)//shimmer的倾斜角度
                    .frozen(true)//true则表示显示骨架屏时，RecyclerView不可滑动，否则可以滑动
                    .duration(1200)//动画时间，以毫秒为单位
                    .count(10)//显示骨架屏时item的个数
                    .load(this.skeletonLayout == 0 ? R.layout.default_skeleton_news_item : this.skeletonLayout)//骨架屏UI
                    .show(); //default count is 1
        } else {
            if (skeletonView == null) try {
                throw new Exception("未设置骨架屏View");
            } catch (Exception e) {
                e.printStackTrace();
            }
            skeletonScreen = Skeleton.bind(skeletonView)
                    .shimmer(true)//是否开启动画
                    .angle(30)//shimmer的倾斜角度
                    .duration(1200)//动画时间，以毫秒为单位
                    .load(this.skeletonLayout == 0 ? R.layout.default_skeleton_news_item : this.skeletonLayout)//骨架屏UI
                    .show(); //default count is 1
        }
    }
    /**
     * =====================================设置公共方法============================
     */

    /**
     * 设置需要获取的权限
     */
    public void RequestPermissions(OnPermissionListener listener, String... permissions) {
        PermissionX.init(this)
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(deniedList, "即将重新申请的权限是程序必须依赖的权限", "我已明白", "取消");
                })
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白", "取消");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        listener.onPermissionGranted();
                    } else {
                        listener.onPermissionDenied(deniedList);
                    }
                });
    }

    public MultipleStatusView getMultipleStatusView() {
        if (!enableMultiple) {
            throw new RuntimeException("noMultipleView");
        }
        return multipleStatusView;
    }

    /**
     * 设置是否显示MultipleView
     *
     * @param enableMultiple
     */
    public void setEnableMultiple(boolean enableMultiple) {
        this.enableMultiple = enableMultiple;
    }

    /**
     * 设置是否显示骨架屏
     *
     * @param enableSkeleton
     */
    public void setEnableSkeletonScreen(boolean enableSkeleton, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        this.enableSkeletonScreen = enableSkeleton;
        this.skeletonRecyclerview = recyclerView;
        this.skeletonAdapter = adapter;
    }

    public void setEnableSkeletonScreen(boolean enableSkeleton, View view) {
        this.enableSkeletonScreen = enableSkeleton;
        this.skeletonView = view;
    }

    /**
     * 设置显示骨架(需要在setEnableSkeletonScreen之前设置才有效)
     *
     * @param skeletonLayout
     */
    public void setSkeletonLayout(@LayoutRes int skeletonLayout) {
        this.skeletonLayout = skeletonLayout;
    }

    /**
     * 获取骨架屏对象
     *
     * @return SkeletonScreen
     */
    public void hideSkeletonScreen() throws Exception {
        if (skeletonScreen == null) throw new Exception("skeletonScreen为空");
        skeletonScreen.hide();
    }

    /**
     * 显示dialog
     */
    public void showProgressDialog(String msg) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showProgressDialog(msg);
        }
    }

    /**
     * 隐藏dialog
     */
    public void dismissProgressDialog() {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).dismissProgressDialog();
        }
    }

}

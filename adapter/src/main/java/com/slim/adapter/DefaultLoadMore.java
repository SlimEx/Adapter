package com.slim.adapter;

/**
 * 项目：SlimAdapter
 * 作者：Yuki - 2018/3/10
 */
public class DefaultLoadMore extends SlimLoadMore {
    @Override
    public int getLayoutRes() {
        return R.layout.view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    public int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}

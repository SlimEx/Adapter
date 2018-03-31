package com.slim.adapter;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

/**
 * 项目：SlimAdapter
 * 作者：Yuki - 2018/3/4
 */

public abstract class SlimLoadMore {
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_FAIL = 2;
    public static final int STATUS_END = 3;
    private int mLoadMoreStatus = 1;

    public void setLoadMoreStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }


    public void convert(SlimViewHolder holder) {
        switch (mLoadMoreStatus) {
            case STATUS_LOADING:
                visibleLoading(holder, true);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_FAIL:
                visibleLoading(holder, false);
                visibleLoadFail(holder, true);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_END:
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, true);
                break;
        }
    }


    private void visibleLoading(SlimViewHolder holder, boolean visible) {
        holder.visibility(getLoadingViewId(), visible);
    }

    private void visibleLoadFail(SlimViewHolder holder, boolean visible) {
        holder.visibility(getLoadFailViewId(), visible);
    }

    private void visibleLoadEnd(SlimViewHolder holder, boolean visible) {
        holder.visibility(getLoadEndViewId(), visible);
    }


    /**
     * load more layout
     *
     * @return
     */

    public abstract @LayoutRes
    int getLayoutRes();

    /**
     * loading view
     *
     * @return
     */

    protected abstract @IdRes
    int getLoadingViewId();


    /**
     * load fail view
     *
     * @return
     */

    public abstract @IdRes
    int getLoadFailViewId();

    /**
     * load end view, you can return 0
     *
     * @return
     */
    protected abstract @IdRes
    int getLoadEndViewId();
}

package com.slim.adapter.ex;

import android.view.View;

/**
 * 项目：SlimAdapter
 * 作者：Yuki - 2018/3/6
 */
public interface OnItemLongClickListener<T> {
    void onLongClick(View view, T item, int position);
}

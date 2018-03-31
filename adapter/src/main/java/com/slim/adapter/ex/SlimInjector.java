package com.slim.adapter.ex;

import com.slim.adapter.SlimViewHolder;

/**
 * 项目：SlimAdapter
 * 作者：Yuki - 2018/3/5
 */
public interface SlimInjector<T> {
    void onInject(SlimViewHolder holder, T item, int position);
}

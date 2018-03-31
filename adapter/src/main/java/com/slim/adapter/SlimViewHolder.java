package com.slim.adapter;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 项目：SlimAdapter
 * 作者：Yuki - 2018/3/4
 */

public abstract class SlimViewHolder extends ViewHolder {
    private SparseArray<View> viewCache;

    public SlimViewHolder(ViewGroup parent, int itemLayoutRes) {
        this(LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false));
    }

    public SlimViewHolder(View view) {
        super(view);
    }


    public <V extends View> V find(int id) {
        if (viewCache == null)
            viewCache = new SparseArray<>();
        View view = viewCache.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            viewCache.put(id, view);
        }
        return (V) view;
    }

    public SlimViewHolder text(int id, int res) {
        TextView view = find(id);
        view.setText(res);
        return this;
    }

    public SlimViewHolder text(int id, CharSequence charSequence) {
        TextView view = find(id);
        view.setText(charSequence);
        return this;
    }

    public SlimViewHolder typeface(int id, Typeface typeface, int style) {
        TextView view = find(id);
        view.setTypeface(typeface, style);
        return this;
    }

    public SlimViewHolder typeface(int id, Typeface typeface) {
        TextView view = find(id);
        view.setTypeface(typeface);
        return this;
    }

    public SlimViewHolder textColor(int id, int color) {
        TextView view = find(id);
        view.setTextColor(color);
        return this;
    }

    public SlimViewHolder textSize(int id, int sp) {
        TextView view = find(id);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        return this;
    }

    public SlimViewHolder alpha(int id, float alpha) {
        View view = find(id);
        view.setAlpha(alpha);
        return this;
    }


    public SlimViewHolder image(int id, int res) {
        ImageView view = find(id);
        view.setImageResource(res);
        return this;
    }

    public SlimViewHolder image(int id, Drawable drawable) {
        ImageView view = find(id);
        view.setImageDrawable(drawable);
        return this;
    }

    public SlimViewHolder background(int id, int res) {
        View view = find(id);
        view.setBackgroundResource(res);
        return this;
    }

    @SuppressWarnings("deprecation")
    public SlimViewHolder background(int id, Drawable drawable) {
        View view = find(id);
        view.setBackgroundDrawable(drawable);
        return this;
    }


    public SlimViewHolder visibility(int id, boolean visible) {
        find(id).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public SlimViewHolder gone(int id, boolean visible) {
        find(id).setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public SlimViewHolder clicked(int id, View.OnClickListener listener) {
        find(id).setOnClickListener(listener);
        return this;
    }

    public SlimViewHolder longClicked(int id, View.OnLongClickListener listener) {
        find(id).setOnLongClickListener(listener);
        return this;
    }

    public SlimViewHolder enable(int id, boolean enable) {
        find(id).setEnabled(enable);
        return this;
    }

    public SlimViewHolder enable(int id) {
        find(id).setEnabled(true);
        return this;
    }

    public SlimViewHolder disable(int id) {
        find(id).setEnabled(false);
        return this;
    }

    public SlimViewHolder checked(int id, boolean checked) {
        Checkable view = find(id);
        view.setChecked(checked);
        return this;
    }

    public SlimViewHolder selected(int id, boolean selected) {
        find(id).setSelected(selected);
        return this;
    }

    public SlimViewHolder pressed(int id, boolean pressed) {
        find(id).setPressed(pressed);
        return this;
    }

    public abstract void init(SlimAdapter adapter, int position);

}

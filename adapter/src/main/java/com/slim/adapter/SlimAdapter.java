package com.slim.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.slim.adapter.ex.MultipleType;
import com.slim.adapter.ex.OnItemClickListener;
import com.slim.adapter.ex.OnItemLongClickListener;
import com.slim.adapter.ex.OnLoadMoreListener;
import com.slim.adapter.ex.SlimInjector;
import com.slim.adapter.ex.SpanSizeLookup;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * 项目：SlimAdapter
 * 作者：Yuki - 2018/3/4
 */
public class SlimAdapter<T> extends RecyclerView.Adapter<SlimViewHolder> {

    private final int TYPE_MORE = -2;
    private final int TYPE_HEAD = 1;
    private final int TYPE_EMPTY = 0;
    private final int TYPE_FOOT = -1;
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private FrameLayout mEmptyLayout;
    private SlimLoadMore mLoadMoreView;
    private OnLoadMoreListener mLoadMoreListener;
    private List<T> mData;
    private MultipleType<T> mMultipleType;
    private boolean mLoading;
    private RecyclerView mRecyclerView;
    private OnItemClickListener<T> mOnClickListener;
    private OnItemLongClickListener<T> mOnLongClickListener;
    private SpanSizeLookup mSpanSizeLookup;
    private SlimInjector<T> mSlimInjector;

    private SlimAdapter() {
    }


    public static <T> SlimAdapter<T> create(RecyclerView recyclerView) {
        SlimAdapter<T> adapter = new SlimAdapter<>();
        adapter.mRecyclerView = recyclerView;
        if (recyclerView.getLayoutManager() != null)
            recyclerView.setAdapter(adapter);
        return adapter;
    }

    public SlimAdapter<T> multiple(MultipleType<T> multipleType) {
        if (this.mMultipleType != null)
            throw new RuntimeException("请先设置multiple开启多布局");
        this.mMultipleType = multipleType;
        return this;
    }

    public SlimAdapter<T> register(final int layoutRes, SlimInjector<T> slimInjector) {
        if (this.mMultipleType != null)
            throw new RuntimeException("此为单布局注册方法,请使用多布局注册方法");
        this.mMultipleType = new MultipleType<T>() {
            @Override
            public int getLayoutId(T item, int position) {
                return layoutRes;
            }
        };
        this.mSlimInjector = slimInjector;
        return this;
    }

    public SlimAdapter<T> register(SlimInjector<T> slimInjector) {
        if (this.mMultipleType == null)
            throw new RuntimeException("请先设置multiple开启多布局");
        mSlimInjector = slimInjector;
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        if (getDataCount() == 0 && mEmptyLayout != null)
            return TYPE_EMPTY;
        else if (position < getHeaderCount())
            return TYPE_HEAD;
        else if (position < getHeaderCount() + getDataCount())
            return mMultipleType.getLayoutId(mData.get(position - getHeaderCount()), position - getHeaderCount());
        else if (position < getHeaderCount() + getDataCount() + getFooterCount())
            return TYPE_FOOT;
        else if (mLoadMoreListener != null)
            return TYPE_MORE;
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public SlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_EMPTY:
                return new SlimViewHolderEx(mEmptyLayout);
            case TYPE_HEAD:
                return new SlimViewHolderEx(mHeaderLayout);
            case TYPE_FOOT:
                return new SlimViewHolderEx(mFooterLayout);
            case TYPE_MORE:
                return new SlimViewHolder(parent, mLoadMoreView.getLayoutRes()) {
                    @Override
                    public void init(SlimAdapter adapter, int position) {
                        mLoadMoreView.convert(this);
                        clicked(mLoadMoreView.getLoadFailViewId(), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadMoreComplete();
                                mLoadMoreListener.onLoadMore();
                            }
                        });
                    }
                };
            default:
                return new SlimViewHolder(parent, viewType) {
                    @Override
                    public void init(final SlimAdapter adapter, final int position) {
                        if (mOnClickListener != null)
                            itemView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mOnClickListener.onClick(v, getItem(position), position);
                                }
                            });

                        if (mOnLongClickListener != null)
                            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    mOnLongClickListener.onLongClick(v, getItem(position), position);
                                    return false;
                                }
                            });

                        mSlimInjector.onInject(this, getItem(position), position);
                    }
                };
        }
    }


    @Override
    public void onBindViewHolder(@NonNull SlimViewHolder holder, int position) {
        //更改真实数据时需减去头布局的,否则数据会错误1个
        int positionEx = (position >= getHeaderCount() && position < getHeaderCount() + getDataCount()) ? position - getHeaderCount() : position;
        holder.init(this, positionEx);

    }

    @Override
    public int getItemCount() {
        if (getDataCount() == 0)
            return mEmptyLayout != null ? 1 : 0;
        return getHeaderCount() + getDataCount() + getLoadMoreCount() + getFooterCount();
    }

    public SlimAdapter<T> empty(@LayoutRes int layoutRes) {
        empty(View.inflate(mRecyclerView.getContext(), layoutRes, null));
        return this;
    }

    public SlimAdapter<T> empty(@LayoutRes int layoutRes, OnClickListener onClickListener) {
        empty(View.inflate(mRecyclerView.getContext(), layoutRes, mRecyclerView), onClickListener);
        return this;
    }

    public SlimAdapter<T> empty(View view) {
        empty(view, null);
        return this;
    }

    public SlimAdapter<T> empty(View view, OnClickListener onClickListener) {
        if (mEmptyLayout == null) {
            mEmptyLayout = new FrameLayout(view.getContext());
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyLayout.setLayoutParams(layoutParams);
        }
        if (onClickListener != null)
            view.setOnClickListener(onClickListener);
        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(view);
        return this;
    }

    public SlimAdapter<T> head(@LayoutRes int layoutRes) {
        View view = LayoutInflater.from(mRecyclerView.getContext()).inflate(layoutRes, (ViewGroup) mRecyclerView.getParent(), false);
        head(view);
        return this;
    }

    public SlimAdapter<T> head(View view) {
        head(view, -1);
        return this;
    }

    public SlimAdapter<T> head(View view, int index) {
        head(view, index, LinearLayout.VERTICAL);
        return this;
    }

    public SlimAdapter<T> head(View view, int index, int orientation) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(mRecyclerView.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        int childCount = mHeaderLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mHeaderLayout.addView(view, index);

        if (mHeaderLayout.getChildCount() == 1) {
            if (getDataCount() != 0) {
                notifyItemInserted(0);
            }
        }
        return this;
    }

    public void removeFootView(View view) {
        if (getFooterCount() == 0)
            return;
        mFooterLayout.removeView(view);
        if (mFooterLayout.getChildCount() == 0) {
            int position = getLoadMoreViewPosition() - 1;
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    public void removeHeadView(View view) {
        if (getHeaderCount() == 0)
            return;
        mHeaderLayout.removeView(view);
        if (mHeaderLayout.getChildCount() == 0) {
            int position = getHeaderCount() - 1;
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    public SlimAdapter<T> foot(@LayoutRes int layoutRes) {
        View view = View.inflate(mRecyclerView.getContext(), layoutRes, null);
        foot(view);
        return this;
    }

    public SlimAdapter<T> foot(View view) {
        foot(view, -1);
        return this;
    }

    public SlimAdapter<T> foot(View view, int index) {
        foot(view, index, LinearLayout.VERTICAL);
        return this;
    }


    public SlimAdapter<T> foot(View view, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(mRecyclerView.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(view, index);
        if (mFooterLayout.getChildCount() == 1) {
            if (getDataCount() != 0) {
                notifyItemInserted(getLoadMoreViewPosition());
            }
        }
        return this;
    }

    public SlimAdapter<T> layout(@NonNull RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(this);
        return this;
    }

    public SlimAdapter<T> loadMore(@NonNull OnLoadMoreListener loadMoreListener) {
        this.mLoadMoreView = new DefaultLoadMore();
        this.mLoadMoreListener = loadMoreListener;
        return this;
    }

    public SlimAdapter<T> loadMore(SlimLoadMore loadMoreView, @NonNull OnLoadMoreListener loadMoreListener) {
        this.mLoadMoreView = loadMoreView;
        this.mLoadMoreListener = loadMoreListener;
        return this;
    }

    //设置指定位置的占位
    public SlimAdapter<T> span(SpanSizeLookup spanSizeLookup) {
        mSpanSizeLookup = spanSizeLookup;
        return this;
    }


    public SlimAdapter<T> click(OnItemClickListener<T> clickListener) {
        mOnClickListener = clickListener;
        return this;
    }

    public SlimAdapter<T> londClick(OnItemLongClickListener<T> longClickListener) {
        mOnLongClickListener = longClickListener;
        return this;
    }

    public SlimAdapter<T> initNew(@NonNull List<T> newData) {
        mData = newData;
        notifyDataSetChanged();
        return this;
    }

    public SlimAdapter<T> initMore(@NonNull List<T> moreData) {
        mData.addAll(moreData);
        int position = mData.size() - moreData.size() + getHeaderCount();
        notifyItemRangeInserted(position, moreData.size());
        loadMoreComplete();
        return this;
    }


    private int getHeaderCount() {
        return mHeaderLayout == null ? 0 : 1;
    }

    private int getDataCount() {
        return mData == null ? 0 : mData.size();
    }

    private int getFooterCount() {
        return mFooterLayout == null ? 0 : 1;
    }

    private int getLoadMoreCount() {
        return mLoadMoreListener == null ? 0 : 1;
    }

    private int getLoadMoreViewPosition() {
        return getHeaderCount() + getDataCount() + getFooterCount();
    }

    public void loadMoreFail() {
        if (mLoadMoreListener == null)
            throw new RuntimeException("请先启动loadMore");
        mLoadMoreView.setLoadMoreStatus(SlimLoadMore.STATUS_FAIL);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    public void loadMoreEnd() {
        if (mLoadMoreListener == null)
            throw new RuntimeException("请先启动loadMore");
        mLoadMoreView.setLoadMoreStatus(SlimLoadMore.STATUS_END);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    private void loadMoreComplete() {
        if (mLoadMoreListener == null)
            throw new RuntimeException("请先启动loadMore");
        mLoadMoreView.setLoadMoreStatus(SlimLoadMore.STATUS_LOADING);
        mLoading = false;
        notifyItemChanged(getLoadMoreViewPosition());

    }

    public T getItem(int position) {
        if (position >= 0 && position < mData.size())
            return mData.get(position);
        else
            return null;
    }

    public List<T> getData() {
        return mData;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new OnScrollListener() {
            //另一种实现上拉的代码不再滚动状态时实现
            //            @Override
            //            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //                super.onScrollStateChanged(recyclerView, newState);
            //                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && !mLoading)
            //                    if (mLoadMoreListener != null) {
            //                        System.out.println("loadMore");
            //                        mLoading = true;
            //                        mLoadMoreListener.onLoadMore();
            //                    }
            //            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //当前不能继续上拉同时不在加载更多状态
                if (!recyclerView.canScrollVertically(1) && loadMoreEnabled()) {
                    mLoading = true;
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadMoreListener.onLoadMore();
                        }
                    });
                }
            }


        });

        if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) mRecyclerView.getLayoutManager());
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position < getHeaderCount() || position >= getHeaderCount() + getDataCount() ? gridManager.getSpanCount() : mSpanSizeLookup == null ? 1 : mSpanSizeLookup.getSpanSize(gridManager, position - getHeaderCount());
                }
            });
        }
    }

    //不在加载更多,监听不为null,存在正常数据源
    private boolean loadMoreEnabled() {
        return !mLoading && mLoadMoreListener != null && getDataCount() != 0;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SlimViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getAdapterPosition();
        if (position < getHeaderCount() || position >= getHeaderCount() + getDataCount()) {
            setFullSpan(holder);
        }
    }


    private void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }
}

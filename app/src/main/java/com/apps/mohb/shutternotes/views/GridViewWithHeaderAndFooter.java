/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : GridViewWithHeaderAndFooter.java
 *  Last modified : 6/26/24, 11:07 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.views;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * A {@link GridView} that supports adding header rows in a
 * very similar way to {@link android.widget.ListView}.
 * See {@link GridViewWithHeaderAndFooter#addHeaderView(View, Object, boolean)}
 * See {@link GridViewWithHeaderAndFooter#addFooterView(View, Object, boolean)}
 */
public class GridViewWithHeaderAndFooter extends GridView {

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    static class FixedViewInfo {
        public ViewGroup viewContainer;
        /**
         * The data backing the view. This is returned from {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed view should be selectable in the grid
         */
        public boolean isSelectable;
    }

    private View mViewForMeasureRowHeight = null;
    private int mRowHeight = -1;

    private final ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();
    private final ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<>();
    private ItemClickHandler mItemClickHandler;

    public GridViewWithHeaderAndFooter(Context context) {
        super(context);
    }

    public GridViewWithHeaderAndFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewWithHeaderAndFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter = getAdapter();
        if (adapter instanceof HeaderViewGridAdapter) {
            ((HeaderViewGridAdapter) adapter).setNumColumns(getNumColumnsCompatible());
            ((HeaderViewGridAdapter) adapter).setRowHeight(getRowHeight());
        }
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
        // Ignore, since the header rows depend on not being clipped
    }

    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p/>
     * NOTE: Call this before calling setAdapter. This is so HeaderGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v The view to add.
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p/>
     * NOTE: Call this before calling setAdapter. This is so HeaderGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();
        if (adapter != null && !(adapter instanceof HeaderViewGridAdapter)) {
            throw new IllegalStateException(
                    "Cannot add header view to grid -- setAdapter has already been called.");
        }

        ViewGroup.LayoutParams lyp = v.getLayoutParams();

        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());

        if (lyp != null) {
            v.setLayoutParams(new FrameLayout.LayoutParams(lyp.width, lyp.height));
            fl.setLayoutParams(new AbsListView.LayoutParams(lyp.width, lyp.height));
        }
        fl.addView(v);
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            ((HeaderViewGridAdapter) adapter).notifyDataSetChanged();
        }
    }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        ListAdapter mAdapter = getAdapter();
        if (mAdapter != null && !(mAdapter instanceof HeaderViewGridAdapter)) {
            throw new IllegalStateException(
                    "Cannot add header view to grid -- setAdapter has already been called.");
        }

        ViewGroup.LayoutParams lyp = v.getLayoutParams();

        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());

        if (lyp != null) {
            v.setLayoutParams(new FrameLayout.LayoutParams(lyp.width, lyp.height));
            fl.setLayoutParams(new AbsListView.LayoutParams(lyp.width, lyp.height));
        }
        fl.addView(v);
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);

        if (mAdapter != null) {
            ((HeaderViewGridAdapter) mAdapter).notifyDataSetChanged();
        }
    }

    public int getHeaderViewCount() {
        return mHeaderViewInfos.size();
    }

    private int getNumColumnsCompatible() {
        return super.getNumColumns();
    }

    private int getColumnWidthCompatible() {
        return super.getColumnWidth();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mViewForMeasureRowHeight = null;
    }

    public int getVerticalSpacing() {
        int value = 0;
        try {
            value = super.getVerticalSpacing();
        } catch (Exception ignore) {
        }

        return value;
    }

    public int getHorizontalSpacing() {
        int value = 0;
        try {
            value = super.getHorizontalSpacing();
        } catch (Exception ignore) {
        }

        return value;
    }

    public int getRowHeight() {
        if (mRowHeight > 0) {
            return mRowHeight;
        }
        ListAdapter adapter = getAdapter();
        int numColumns = getNumColumnsCompatible();

        // adapter has not been set or has no views in it;
        if (adapter == null || adapter.getCount() <= numColumns * (mHeaderViewInfos.size() + mFooterViewInfos.size())) {
            return -1;
        }
        int mColumnWidth = getColumnWidthCompatible();
        View view = getAdapter().getView(numColumns * mHeaderViewInfos.size(), mViewForMeasureRowHeight, this);
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) view.getLayoutParams();
        if (p == null) {
            p = new AbsListView.LayoutParams(-1, -2, 0);
            view.setLayoutParams(p);
        }
        int childHeightSpec = getChildMeasureSpec(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.height);
        int childWidthSpec = getChildMeasureSpec(
                MeasureSpec.makeMeasureSpec(mColumnWidth, MeasureSpec.EXACTLY), 0, p.width);
        view.measure(childWidthSpec, childHeightSpec);
        mViewForMeasureRowHeight = view;
        mRowHeight = view.getMeasuredHeight();
        return mRowHeight;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!mHeaderViewInfos.isEmpty() || !mFooterViewInfos.isEmpty()) {
            HeaderViewGridAdapter headerViewGridAdapter = new HeaderViewGridAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
            int numColumns = getNumColumnsCompatible();
            if (numColumns > 1) {
                headerViewGridAdapter.setNumColumns(numColumns);
            }
            headerViewGridAdapter.setRowHeight(getRowHeight());
            super.setAdapter(headerViewGridAdapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    /*
     * full width
     */
    private class FullWidthFixedViewLayout extends FrameLayout {

        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int realLeft = GridViewWithHeaderAndFooter.this.getPaddingLeft() + getPaddingLeft();
            // Try to make where it should be, from left, full width
            if (realLeft != left) {
                offsetLeftAndRight(realLeft - left);
            }
            super.onLayout(changed, left, top, right, bottom);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int targetWidth = GridViewWithHeaderAndFooter.this.getMeasuredWidth()
                    - GridViewWithHeaderAndFooter.this.getPaddingLeft()
                    - GridViewWithHeaderAndFooter.this.getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth,
                    MeasureSpec.getMode(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        ListAdapter adapter = getAdapter();
        if (adapter instanceof HeaderViewGridAdapter) {
            ((HeaderViewGridAdapter) adapter).setNumColumns(numColumns);
        }
    }

    /*
     * ListAdapter used when a HeaderGridView has header views. This ListAdapter
     * wraps another one and also keeps track of the header views and their
     * associated data objects.
     * <p>This is intended as a base class; you will probably not need to
     * use this class directly in your own code.
     */
    private static class HeaderViewGridAdapter implements WrapperListAdapter, Filterable {
        // This is used to notify the container of updates relating to number of columns
        // or headers changing, which changes the number of placeholders needed
        private final DataSetObservable mDataSetObservable = new DataSetObservable();
        private final ListAdapter mAdapter;
        static final ArrayList<FixedViewInfo> EMPTY_INFO_LIST =
                new ArrayList<>();

        // This ArrayList is assumed to NOT be null.
        final ArrayList<FixedViewInfo> mHeaderViewInfos;
        final ArrayList<FixedViewInfo> mFooterViewInfos;
        private int mNumColumns = 1;
        private int mRowHeight = -1;
        final boolean mAreAllFixedViewsSelectable;
        private final boolean mIsFilterable;
        private final boolean mCachePlaceHoldView = true;
        // From Recycle Bin or calling getView, this a question...
        private final boolean mCacheFirstHeaderView = false;

        public HeaderViewGridAdapter(ArrayList<FixedViewInfo> headerViewInfos, ArrayList<FixedViewInfo> footViewInfos, ListAdapter adapter) {
            mAdapter = adapter;
            mIsFilterable = adapter instanceof Filterable;
            if (headerViewInfos == null) {
                mHeaderViewInfos = EMPTY_INFO_LIST;
            } else {
                mHeaderViewInfos = headerViewInfos;
            }

            if (footViewInfos == null) {
                mFooterViewInfos = EMPTY_INFO_LIST;
            } else {
                mFooterViewInfos = footViewInfos;
            }
            mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos)
                    && areAllListInfosSelectable(mFooterViewInfos);
        }

        public void setNumColumns(int numColumns) {
            if (numColumns < 1) {
                return;
            }
            if (mNumColumns != numColumns) {
                mNumColumns = numColumns;
                notifyDataSetChanged();
            }
        }

        public void setRowHeight(int height) {
            mRowHeight = height;
        }

        public int getHeadersCount() {
            return mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return mFooterViewInfos.size();
        }

        /**
         * @return true if this adapter doesn't contain any data.  This is used to determine
         * whether the empty view should be displayed.  A typical implementation will return
         * getCount() == 0 but since getCount() includes the headers and footers, specialized
         * adapters might want a different behavior.
         */
        @Override
        public boolean isEmpty() {
            return (mAdapter == null || mAdapter.isEmpty());
        }

        private boolean areAllListInfosSelectable(ArrayList<FixedViewInfo> infos) {
            if (infos != null) {
                for (FixedViewInfo info : infos) {
                    if (!info.isSelectable) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public int getCount() {
            if (mAdapter != null) {
                return (getFootersCount() + getHeadersCount()) * mNumColumns + getAdapterAndPlaceHolderCount();
            } else {
                return (getFootersCount() + getHeadersCount()) * mNumColumns;
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return mAdapter == null || mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
        }

        private int getAdapterAndPlaceHolderCount() {
            return (int) (Math.ceil(1f * mAdapter.getCount() / mNumColumns) * mNumColumns);
        }

        @Override
        public boolean isEnabled(int position) {
            // Header (negative positions will throw an IndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                return position % mNumColumns == 0
                        && mHeaderViewInfos.get(position / mNumColumns).isSelectable;
            }

            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition < adapterCount) {
                    return adjPosition < mAdapter.getCount() && mAdapter.isEnabled(adjPosition);
                }
            }

            // Footer (off-limits positions will throw an IndexOutOfBoundsException)
            final int footerPosition = adjPosition - adapterCount;
            return footerPosition % mNumColumns == 0
                    && mFooterViewInfos.get(footerPosition / mNumColumns).isSelectable;
        }

        @Override
        public Object getItem(int position) {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                if (position % mNumColumns == 0) {
                    return mHeaderViewInfos.get(position / mNumColumns).data;
                }
                return null;
            }

            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition < adapterCount) {
                    if (adjPosition < mAdapter.getCount()) {
                        return mAdapter.getItem(adjPosition);
                    } else {
                        return null;
                    }
                }
            }

            // Footer (off-limits positions will throw an IndexOutOfBoundsException)
            final int footerPosition = adjPosition - adapterCount;
            if (footerPosition % mNumColumns == 0) {
                return mFooterViewInfos.get(footerPosition).data;
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (mAdapter != null && position >= numHeadersAndPlaceholders) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                View headerViewContainer = mHeaderViewInfos
                        .get(position / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return headerViewContainer;
                } else {
                    if (convertView == null) {
                        convertView = new View(parent.getContext());
                    }
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(headerViewContainer.getHeight());
                    return convertView;
                }
            }
            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition < adapterCount) {
                    if (adjPosition < mAdapter.getCount()) {
                        return mAdapter.getView(adjPosition, convertView, parent);
                    } else {
                        if (convertView == null) {
                            convertView = new View(parent.getContext());
                        }
                        convertView.setVisibility(View.INVISIBLE);
                        convertView.setMinimumHeight(mRowHeight);
                        return convertView;
                    }
                }
            }
            // Footer
            final int footerPosition = adjPosition - adapterCount;
            if (footerPosition < getCount()) {
                View footViewContainer = mFooterViewInfos
                        .get(footerPosition / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return footViewContainer;
                } else {
                    if (convertView == null) {
                        convertView = new View(parent.getContext());
                    }
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(footViewContainer.getHeight());
                    return convertView;
                }
            }
            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public int getItemViewType(int position) {

            final int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            final int adapterViewTypeStart = mAdapter == null ? 0 : mAdapter.getViewTypeCount() - 1;
            int type = AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
            if (mCachePlaceHoldView) {
                // Header
                if (position < numHeadersAndPlaceholders) {
                    if (position == 0) {
                        if (mCacheFirstHeaderView) {
                            type = adapterViewTypeStart + mHeaderViewInfos.size() + mFooterViewInfos.size() + 1 + 1;
                        }
                    }
                    if (position % mNumColumns != 0) {
                        type = adapterViewTypeStart + (position / mNumColumns + 1);
                    }
                }
            }

            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition >= 0 && adjPosition < adapterCount) {
                    if (adjPosition < mAdapter.getCount()) {
                        type = mAdapter.getItemViewType(adjPosition);
                    } else {
                        if (mCachePlaceHoldView) {
                            type = adapterViewTypeStart + mHeaderViewInfos.size() + 1;
                        }
                    }
                }
            }

            if (mCachePlaceHoldView) {
                // Footer
                final int footerPosition = adjPosition - adapterCount;
                if (footerPosition >= 0 && footerPosition < getCount() && (footerPosition % mNumColumns) != 0) {
                    type = adapterViewTypeStart + mHeaderViewInfos.size() + 1 + (footerPosition / mNumColumns + 1);
                }
            }
            return type;
        }

        /**
         * content view, content view holder, header[0], header and footer placeholder(s)
         */
        @Override
        public int getViewTypeCount() {
            int count = mAdapter == null ? 1 : mAdapter.getViewTypeCount();
            if (mCachePlaceHoldView) {
                int offset = mHeaderViewInfos.size() + 1 + mFooterViewInfos.size();
                if (mCacheFirstHeaderView) {
                    offset += 1;
                }
                count += offset;
            }
            return count;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        @Override
        public Filter getFilter() {
            if (mIsFilterable) {
                return ((Filterable) mAdapter).getFilter();
            }
            return null;
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return mAdapter;
        }

        public void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
        super.setOnItemClickListener(getItemClickHandler());
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        super.setOnItemLongClickListener(getItemClickHandler());
    }

    private ItemClickHandler getItemClickHandler() {
        if (mItemClickHandler == null) {
            mItemClickHandler = new ItemClickHandler();
        }
        return mItemClickHandler;
    }

    private class ItemClickHandler implements android.widget.AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnItemClickListener != null) {
                int resPos = position - getHeaderViewCount() * getNumColumnsCompatible();
                if (resPos >= 0) {
                    mOnItemClickListener.onItemClick(parent, view, resPos, id);
                }
            }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mOnItemLongClickListener != null) {
                int resPos = position - getHeaderViewCount() * getNumColumnsCompatible();
                if (resPos >= 0) {
                    mOnItemLongClickListener.onItemLongClick(parent, view, resPos, id);
                }
            }
            return true;
        }
    }
}


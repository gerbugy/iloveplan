package com.iloveplan.android.asis.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public final class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final Adapter mAdapter;
    private final boolean mLongPressDragEnabled;
    private final boolean mItemViewSwipeEnabled;

    public ItemTouchHelperCallback(Adapter adapter, boolean itemViewSwipeEnabled, boolean longPressDragEnabled) {
        mAdapter = adapter;
        mItemViewSwipeEnabled = itemViewSwipeEnabled;
        mLongPressDragEnabled = longPressDragEnabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mLongPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mItemViewSwipeEnabled;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        } else {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return viewHolder.getItemViewType() == target.getItemViewType() && mAdapter.onItemMove(recyclerView, viewHolder, target);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemSwiped(viewHolder, direction);
    }

    public interface Adapter {

        boolean onItemMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);

        void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction);
    }
}

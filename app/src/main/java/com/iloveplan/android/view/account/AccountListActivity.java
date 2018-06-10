package com.iloveplan.android.view.account;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.iloveplan.android.Constants;
import com.iloveplan.android.R;
import com.iloveplan.android.util.SQLiteItemUtils;
import com.iloveplan.android.asis.widget.ItemTouchHelperCallback;
import com.iloveplan.android.db.AccountDao;
import com.iloveplan.android.db.SQLiteItem;
import com.iloveplan.android.view.BaseActivity;
import com.iloveplan.android.databinding.AccountListBinding;
import com.iloveplan.android.databinding.AccountListItemBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountListActivity extends BaseActivity implements View.OnClickListener {

    private final Handler mHandler = new Handler();
    private final List<SQLiteItem> mItems = new ArrayList<>();
    private AccountListBinding mBinding;
    private RecyclerViewAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.account_list);
        mBinding.floatingActionButton.setOnClickListener(this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAdapter = new RecyclerViewAdapter());
        mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter, false, true));
        mItemTouchHelper.attachToRecyclerView(mBinding.recyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button:
                startActivityForResult(new Intent(this, AccountEditActivity.class), Constants.REQUEST_INSERT);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mItems.isEmpty()) {
            select(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.RESULT_INSERTED:
                long insertedId = data.getLongExtra(AccountDao.Columns._ID, Constants.NO_ID);
                int insertedPosition = 0;
                mItems.add(insertedPosition, AccountDao.getInstance().select(insertedId));
                mAdapter.notifyItemInserted(insertedPosition);
                mBinding.recyclerView.smoothScrollToPosition(insertedPosition);
                break;
            case Constants.RESULT_CHANGED:
                long changedId = data.getLongExtra(AccountDao.Columns._ID, Constants.NO_ID);
                int changedPosition = SQLiteItemUtils.findPosition(mItems, changedId);
                mItems.set(changedPosition, AccountDao.getInstance().select(changedId));
                mAdapter.notifyItemChanged(changedPosition);
                break;
            case Constants.RESULT_REMOVED:
                long removedId = data.getLongExtra(AccountDao.Columns._ID, Constants.NO_ID);
                int removedPosition = SQLiteItemUtils.findPosition(mItems, removedId);
                mItems.remove(removedPosition);
                mAdapter.notifyItemRemoved(removedPosition);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_list, menu);
        initSearchMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initSearchMenu(Menu menu) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setFocusable(false);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI); // 가로모드에서 입력란이 전체를 차지하지 않도록 합니다.
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                select(null);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                select(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void select(String query) {
        mItems.clear();
        mItems.addAll(AccountDao.getInstance().selectList(TextUtils.isEmpty(query) ? null : " name LIKE '%" + query + "%'", "position DESC, created_at DESC", null));
        mAdapter.notifyDataSetChanged();
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperCallback.Adapter {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(AccountListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SQLiteItem item = mItems.get(position);
            holder.binding.name.setText(item.getString(AccountDao.Columns.NAME));
            holder.binding.loginId.setText(item.getString(AccountDao.Columns.LOGIN_ID));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public boolean onItemMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            Collections.swap(mItems, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SQLiteItem fromItem = mItems.get(fromPosition);
                    SQLiteItem toItem = mItems.get(toPosition);
                    int fromPosition = fromItem.getInt(AccountDao.Columns.POSITION);
                    fromItem.put(AccountDao.Columns.POSITION, toItem.getInt(AccountDao.Columns.POSITION));
                    toItem.put(AccountDao.Columns.POSITION, fromPosition);
                    AccountDao.getInstance().updatePosition(fromItem, toItem);
                }
            });
            return true;
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {

            AccountListItemBinding binding;

            ViewHolder(AccountListItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(this);
                binding.icon.setOnTouchListener(this);
            }

            @Override
            public void onClick(View v) {
                final SQLiteItem item = mItems.get(getAdapterPosition());
                switch (v.getId()) {
                    default:
                        Intent intent = new Intent(AccountListActivity.this, AccountViewActivity.class);
                        intent.putExtra(AccountDao.Columns._ID, item.getLong(AccountDao.Columns._ID));
                        startActivityForResult(intent, Constants.REQUEST_VIEW);
                        break;
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mItemTouchHelper.startDrag(this);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                }
                return true;
            }
        }
    }
}
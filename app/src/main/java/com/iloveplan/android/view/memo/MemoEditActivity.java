package com.iloveplan.android.view.memo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.iloveplan.android.Constants;
import com.iloveplan.android.R;
import com.iloveplan.android.databinding.MemoEditBinding;
import com.iloveplan.android.db.MemoDao;
import com.iloveplan.android.db.SQLiteItem;
import com.iloveplan.android.view.BaseActivity;

public class MemoEditActivity extends BaseActivity {

    private SQLiteItem mItem;
    private MemoEditBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.memo_edit);
        long _id = getIntent().getLongExtra(MemoDao.Columns._ID, Constants.NO_ID);
        if (_id > Constants.NO_ID) {
            select(_id);
            setTitle(R.string.memo_edit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                if (mItem == null) {
                    insert();
                } else {
                    update();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void select(long _id) {
        mItem = MemoDao.getInstance().select(_id);
        mBinding.content.setText(mItem.getString(MemoDao.Columns.CONTENT));
    }

    private void insert() {
        mItem = new SQLiteItem();
        mItem.put(MemoDao.Columns.CONTENT, mBinding.content.getText().toString().trim());
        setResult(Constants.RESULT_INSERTED, new Intent().putExtra(MemoDao.Columns._ID, MemoDao.getInstance().insert(mItem)));
        finish();
    }

    private void update() {
        mItem.put(MemoDao.Columns.CONTENT, mBinding.content.getText().toString().trim());
        MemoDao.getInstance().update(mItem);
        setResult(Constants.RESULT_CHANGED, new Intent().putExtra(MemoDao.Columns._ID, mItem.getLong(MemoDao.Columns._ID)));
        finish();
    }
}

package com.iloveplan.android.view.memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.iloveplan.android.Constants;
import com.iloveplan.android.R;
import com.iloveplan.android.databinding.MemoViewBinding;
import com.iloveplan.android.db.MemoDao;
import com.iloveplan.android.db.SQLiteItem;
import com.iloveplan.android.view.BaseActivity;

public class MemoViewActivity extends BaseActivity implements View.OnClickListener {

    private SQLiteItem mItem;
    private MemoViewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.memo_view);
        mBinding.content.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm);
                builder.setMessage(R.string.memo_delete_confirm);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long _id = mItem.getLong(MemoDao.Columns._ID);
                        MemoDao.getInstance().delete(_id);
                        setResult(Constants.RESULT_REMOVED, new Intent().putExtra(MemoDao.Columns._ID, _id));
                        finish();
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                Intent intent = new Intent(this, MemoEditActivity.class);
                intent.putExtra(MemoDao.Columns._ID, mItem.getLong(MemoDao.Columns._ID));
                startActivityForResult(intent, Constants.REQUEST_CHANGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CHANGED) {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItem = MemoDao.getInstance().select(getIntent().getLongExtra(MemoDao.Columns._ID, Constants.NO_ID));
        mBinding.content.setText(mItem.getString(MemoDao.Columns.CONTENT));
    }
}
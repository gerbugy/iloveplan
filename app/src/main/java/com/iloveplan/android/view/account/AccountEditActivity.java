package com.iloveplan.android.view.account;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.iloveplan.android.Constants;
import com.iloveplan.android.R;
import com.iloveplan.android.databinding.AccountEditBinding;
import com.iloveplan.android.db.AccountDao;
import com.iloveplan.android.db.SQLiteItem;
import com.iloveplan.android.view.BaseActivity;

public final class AccountEditActivity extends BaseActivity {

    private SQLiteItem mItem;
    private AccountEditBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // super.setSecure();
        mBinding = DataBindingUtil.setContentView(this, R.layout.account_edit);
        long _id = getIntent().getLongExtra(AccountDao.Columns._ID, Constants.NO_ID);
        if (_id > -1) {
            select(_id);
            setTitle(R.string.account_edit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_edit, menu);
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
        mItem = AccountDao.getInstance().select(_id);
        mBinding.name.setText(mItem.getString(AccountDao.Columns.NAME));
        mBinding.name.setSelection(mBinding.name.getText().length());
        mBinding.loginId.setText(mItem.getString(AccountDao.Columns.LOGIN_ID));
        mBinding.loginPassword.setText(mItem.getString(AccountDao.Columns.LOGIN_PASSWORD));
        mBinding.url.setText(mItem.getString(AccountDao.Columns.URL));
        mBinding.description.setText(mItem.getString(AccountDao.Columns.DESCRIPTION));
    }

    private void insert() {
        mItem = new SQLiteItem();
        mItem.put(AccountDao.Columns.NAME, mBinding.name.getText().toString().trim());
        mItem.put(AccountDao.Columns.LOGIN_ID, mBinding.loginId.getText().toString().trim());
        mItem.put(AccountDao.Columns.LOGIN_PASSWORD, mBinding.loginPassword.getText().toString().trim());
        mItem.put(AccountDao.Columns.URL, mBinding.url.getText().toString().trim());
        mItem.put(AccountDao.Columns.DESCRIPTION, mBinding.description.getText().toString().trim());
        setResult(Constants.RESULT_INSERTED, new Intent().putExtra(AccountDao.Columns._ID, AccountDao.getInstance().insert(mItem)));
        finish();
    }

    private void update() {
        mItem.put(AccountDao.Columns.NAME, mBinding.name.getText().toString().trim());
        mItem.put(AccountDao.Columns.LOGIN_ID, mBinding.loginId.getText().toString().trim());
        mItem.put(AccountDao.Columns.LOGIN_PASSWORD, mBinding.loginPassword.getText().toString().trim());
        mItem.put(AccountDao.Columns.URL, mBinding.url.getText().toString().trim());
        mItem.put(AccountDao.Columns.DESCRIPTION, mBinding.description.getText().toString().trim());
        AccountDao.getInstance().update(mItem);
        setResult(Constants.RESULT_CHANGED, new Intent().putExtra(AccountDao.Columns._ID, mItem.getLong(AccountDao.Columns._ID)));
        finish();
    }
}
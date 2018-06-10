package com.iloveplan.android.view.account;

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
import com.iloveplan.android.databinding.AccountViewBinding;
import com.iloveplan.android.db.AccountDao;
import com.iloveplan.android.db.SQLiteItem;
import com.iloveplan.android.view.BaseActivity;

public final class AccountViewActivity extends BaseActivity implements View.OnClickListener {

    private SQLiteItem mItem;
    private AccountViewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.account_view);
        mBinding.getRoot().setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.setType("text/plain");
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_TEXT, mBinding.name.getText().toString() + "\n" + mBinding.loginId.getText().toString() + "\n" + mBinding.description.getText().toString());
                startActivity(Intent.createChooser(msg, null));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItem = AccountDao.getInstance().select(getIntent().getLongExtra(AccountDao.Columns._ID, Constants.NO_ID));
        mBinding.name.setText(mItem.getString(AccountDao.Columns.NAME, ""));
        mBinding.loginId.setText(mItem.getString(AccountDao.Columns.LOGIN_ID, ""));
        mBinding.loginPassword.setText(mItem.getString(AccountDao.Columns.LOGIN_PASSWORD, ""));
        mBinding.url.setText(mItem.getString(AccountDao.Columns.URL, ""));
        mBinding.description.setText(mItem.getString(AccountDao.Columns.DESCRIPTION, ""));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm);
                builder.setMessage(R.string.confirm);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        long _id = mItem.getLong(AccountDao.Columns._ID);
                        AccountDao.getInstance().delete(_id);
                        setResult(Constants.RESULT_REMOVED, new Intent().putExtra(AccountDao.Columns._ID, _id));
                        finish();
                    }
                }).show();
                break;
            default:
                startActivityForResult(new Intent(this, AccountEditActivity.class).putExtra(AccountDao.Columns._ID, mItem.getLong(AccountDao.Columns._ID)), Constants.REQUEST_CHANGE);
                break;
        }
    }
}

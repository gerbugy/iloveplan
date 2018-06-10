package com.iloveplan.android.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.iloveplan.android.R;
import com.iloveplan.android.util.PermissionUtils;

public abstract class BasePermissionActivity extends BaseActivity {

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String[] deniedPermissions = PermissionUtils.getNotAllowedPermissions(this, permissions);
        if (deniedPermissions.length == 0) {
            onRequestPermissionsGranted(requestCode, permissions);
        } else if (!PermissionUtils.hasShouldShowRequestPermissionRationale(this, deniedPermissions)) {
            showCustomRationale(requestCode, deniedPermissions);
        }
    }

    protected void showCustomRationale(final int requestCode, String[] deniedPermissions) {
        StringBuilder labels = new StringBuilder();
        for (String label : PermissionUtils.getPermissionGroupLabels(getPackageManager(), deniedPermissions)) {
            labels.append(labels.length() == 0 ? "" : ", ").append(label);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.required_permissions, labels.toString()));
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(R.string.app_info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName())), requestCode);
            }
        });
        builder.show();
    }

    protected abstract void onRequestPermissionsGranted(int requestCode, String[] permissions);
}
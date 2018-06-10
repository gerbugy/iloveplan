package com.iloveplan.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public final class PermissionUtils {

    public static String[] getNotAllowedPermissions(Context context, String... permissions) {
        List<String> notAllowedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (!notAllowedPermissions.contains(permission)) {
                    notAllowedPermissions.add(permission);
                }
            }
        }
        return notAllowedPermissions.toArray(new String[notAllowedPermissions.size()]);
    }

    public static boolean hasShouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getPermissionGroups(PackageManager packageManager, String[] permissions) {
        List<String> permissionGroups = new ArrayList<>();
        for (String permission : permissions) {
            try {
                String name = packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA).group;
                if (!permissionGroups.contains(name)) {
                    permissionGroups.add(name);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return permissionGroups.toArray(new String[permissionGroups.size()]);
    }

    public static String[] getPermissionGroupLabels(PackageManager packageManager, String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String group : getPermissionGroups(packageManager, permissions)) {
            try {
                String label = packageManager.getPermissionGroupInfo(group, PackageManager.GET_META_DATA).loadLabel(packageManager).toString();
                if (!list.contains(label)) {
                    list.add(label);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list.toArray(new String[list.size()]);
    }
}
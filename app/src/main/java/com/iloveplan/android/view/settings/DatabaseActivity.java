package com.iloveplan.android.view.settings;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import android.widget.Toast;

import com.iloveplan.android.Constants;
import com.iloveplan.android.R;
import com.iloveplan.android.util.BackupUtils;
import com.iloveplan.android.util.DateUtils;
import com.iloveplan.android.util.FileUtils;
import com.iloveplan.android.view.BasePermissionActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends BasePermissionActivity {

    private static final int REQUEST_IMPORT = 1;
    private static final int REQUEST_EXPORT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new DatabaseFragment()).commit();
    }

    @Override
    protected void onRequestPermissionsGranted(int requestCode, String[] permissions) {
        switch (requestCode) {
            case REQUEST_IMPORT:
                importFromDevice(this);
                break;
            case REQUEST_EXPORT:
                exportToDevice(this);
                break;
        }
    }

    private static void importFromDevice(final Context context) {
        List<String> fileNames = new ArrayList<>();
        File[] backupFiles = BackupUtils.getBackupFiles(Constants.HOME_DIRECTORY);
        if (backupFiles != null) {
            for (File file : FileUtils.sortByLastModifiedDescending(backupFiles))
                fileNames.add(file.getName());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.choice);
        builder.setSingleChoiceItems(fileNames.toArray(new String[fileNames.size()]), 0, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView listView = ((AlertDialog) dialog).getListView();
                String selectedName = listView.getAdapter().getItem(listView.getCheckedItemPosition()).toString();
                File selectedFile = new File(new File(Environment.getExternalStorageDirectory(), Constants.HOME_DIRECTORY), selectedName);
                try {
                    boolean imported = new ImportFromDeviceTask().execute(selectedFile, context.getDatabasePath(Constants.DATABASE_NAME)).get();
                    Toast.makeText(context, imported ? R.string.import_from_device_success : R.string.import_from_device_fail, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

    private static void exportToDevice(Context context) {
        try {
            boolean exported = new ExportToDeviceTask().execute(context.getDatabasePath(Constants.DATABASE_NAME)).get();
            Toast.makeText(context, exported ? R.string.export_to_device_success : R.string.export_to_device_fail, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ImportFromDeviceTask extends AsyncTask<File, Void, Boolean> {

        @Override
        protected Boolean doInBackground(File... params) {
            try {
                File source = params[0];
                if (source.exists() && source.isFile()) {
                    FileUtils.copy(source, params[1]);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private static class ExportToDeviceTask extends AsyncTask<File, Void, Boolean> {

        @Override
        protected Boolean doInBackground(File... params) {
            try {
                FileUtils.copy(params[0], new File(new File(Environment.getExternalStorageDirectory(), Constants.HOME_DIRECTORY), DateUtils.currentTime("yyyyMMdd_HHmmss") + ".db"));
                BackupUtils.deleteOldFiles(Constants.HOME_DIRECTORY, 5);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static class DatabaseFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.database_preference);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            switch (preference.getKey()) {
                case "import_from_device":
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        importFromDevice(getActivity());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMPORT);
                    }
                    break;
                case "export_to_device":
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        exportToDevice(getActivity());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXPORT);
                    }
                    break;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}

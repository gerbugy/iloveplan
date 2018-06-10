package com.iloveplan.android.asis.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.iloveplan.android.Constants;
import com.iloveplan.android.R;
import com.iloveplan.android.asis.MainApp;
import com.iloveplan.android.asis.Settings;
import com.iloveplan.android.asis.util.FileUtil;
import com.iloveplan.android.asis.util.PlanUtil;

import java.io.File;

public final class ImportExportDialogFragment extends DialogFragment {

    private Activity mActivity;
    private String mDbPath;
    private String mSdPath;

    public static void show(FragmentManager manager) {
        new ImportExportDialogFragment().show(manager, ImportExportDialogFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mDbPath = File.separator + "data" + File.separator + "data" + File.separator + getActivity().getApplicationContext().getPackageName() + File.separator + "databases" + File.separator + Constants.DATABASE_NAME;
        mSdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Settings.APPLICATION_NAME + File.separator + Constants.DATABASE_NAME;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 다이얼로그를 반환합니다.
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.setting_import_export_title).setItems(R.array.menu_import_export, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 선택메뉴를 취득합니다.
                final String menu = getResources().getStringArray(R.array.menu_import_export)[which];

                // 가져오기를 선택했으면
                if (menu.equals(getString(R.string.setting_import_from_sdcard))) {

                    // SD카드를 체크합니다.
                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        Toast.makeText(mActivity, R.string.setting_import_from_sdcard_fail, Toast.LENGTH_SHORT).show();
                    }
                    // SD카드의 파일을 체크합니다.
                    else if (!new File(mSdPath).exists()) {
                        Toast.makeText(mActivity, R.string.setting_import_from_sdcard_fail, Toast.LENGTH_SHORT).show();
                    }
                    // 최종확인창을 띄웁니다.
                    else {
                        new AlertDialog.Builder(getActivity()).setTitle(R.string.ok).setMessage(R.string.setting_import_from_sdcard_confirm).setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ImportAsyncTask().execute();
                            }
                        }).show();
                    }
                }
                // 내보내기를 선택했으면
                else if (menu.equals(getString(R.string.setting_export_to_sdcard))) {

                    // SD카드를 체크합니다.
                    String state = Environment.getExternalStorageState();
                    if (!Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                        Toast.makeText(mActivity, R.string.setting_export_to_sdcard_fail, Toast.LENGTH_SHORT).show();
                    }
                    // 최종확인창을 띄웁니다.
                    else {
                        new AlertDialog.Builder(getActivity()).setTitle(R.string.ok).setMessage(R.string.setting_export_to_sdcard_confirm).setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ExportAsyncTask().execute();
                            }
                        }).show();
                    }
                }
            }
        }).create();
    }

    /**
     * 가져오기
     */
    private class ImportAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {

            // 진행창을 표시합니다.
            mProgressDialog = ProgressDialog.show(mActivity, null, mActivity.getResources().getString(R.string.waiting));
        }

        @Override
        protected Void doInBackground(Void... params) {

            // 파일을 복사합니다.
            FileUtil.copy(mSdPath, mDbPath);

            // 데이터베이스를 업그레이드합니다.
            MainApp.closeAndOpenDatabase(mActivity);

            // 계획건수정보를 업데이트합니다.
            PlanUtil.updateTotalAndSuccessCount(mActivity);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // 진행창을 닫습니다.
            mProgressDialog.dismiss();

            // 성공메시지를 출력합니다.
            Toast.makeText(mActivity, R.string.setting_import_from_sdcard_success, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 내보내기
     */
    private class ExportAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // 디렉토리를 생성합니다.
            new File(mSdPath).getParentFile().mkdirs();

            // 파일을 복사합니다.
            FileUtil.copy(mDbPath, mSdPath);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // 메시지를 출력합니다.
            Toast.makeText(mActivity, R.string.setting_export_to_sdcard_success, Toast.LENGTH_SHORT).show();
        }
    }
}

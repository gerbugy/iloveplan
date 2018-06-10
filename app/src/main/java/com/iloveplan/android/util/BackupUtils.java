package com.iloveplan.android.util;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public final class BackupUtils {

    private BackupUtils() {

    }

    public static File[] getBackupFiles(String directory) {
        File home = new File(Environment.getExternalStorageDirectory(), directory);
        return home.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile() && name.toLowerCase().endsWith(".db");
            }
        });
    }

    public static void deleteOldFiles(String directory, int threshold) {
        File[] files = BackupUtils.getBackupFiles(directory);
        if (files.length > threshold) {
            Arrays.sort(files);
            for (int i = 0; i < files.length - threshold; i++) {
                if (!files[i].delete()) {
                    break;
                }
            }
        }
    }
}

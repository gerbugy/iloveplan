package com.iloveplan.android.asis.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtil {

    public static boolean copy(String sourcePath, String targetPath) {

        boolean isCopied = false;

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            inputStream = new FileInputStream(sourcePath);
            outputStream = new FileOutputStream(targetPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            isCopied = true;

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (outputStream != null) try { outputStream.close(); } catch (IOException e) { }
            if (inputStream != null) try { inputStream.close(); } catch (IOException e) { }
        }

        return isCopied;
    }
}

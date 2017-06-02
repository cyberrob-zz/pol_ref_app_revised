package com.polstargps.polnav.noodoearoundmereference;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by robert on 2017/6/2.
 */

public class FileUtil {

    public static void copyFolder(final AssetManager assetManager, final String assetsPath, final String folderPath) {
        String[] files = null;
        try {
            files = assetManager.list(assetsPath);
            mkdir(folderPath);
        } catch (final IOException e) {
            Log.e("FileUtil", e.getLocalizedMessage());
        }

        for (String filename : files) {
            InputStream inputStream;
            try {
                inputStream = assetManager.open(assetsPath + File.separator + filename);
            } catch (IOException e) {
                copyFolder(assetManager, assetsPath + File.separator + filename, folderPath + File.separator + filename);
                continue;
            }

            try {
                moveFile(inputStream, folderPath + File.separator + filename);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("FileUtil", e.getLocalizedMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("FileUtil", e.getLocalizedMessage());
                }
            }
        }
    }

    public static void mkdir(final String path) throws IOException {
        final File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void moveFile(final InputStream inputStream, final String destination)
            throws IOException, InterruptedException {
        final File destinationFile = new File(destination);
        if (!destinationFile.exists()) {
            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }
            destinationFile.createNewFile();
        }
        final OutputStream destinationOut = new BufferedOutputStream(new FileOutputStream(destinationFile));

        int numRead;
        byte[] buf = new byte[1024];
        while ((numRead = inputStream.read(buf)) >= 0) {
            destinationOut.write(buf, 0, numRead);
        }

        inputStream.close();
        destinationOut.flush();
        destinationOut.close();

        final String[] args = {"/system/bin/chmod", "755", destination};
        final Process perProcess = new ProcessBuilder(args).start();
        perProcess.waitFor();
        perProcess.destroy();
    }
}

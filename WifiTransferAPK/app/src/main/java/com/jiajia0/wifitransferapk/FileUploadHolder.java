package com.jiajia0.wifitransferapk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Leafage on 2018/2/9 17:57.
 * DESCRIPTION : 文件管理器
 */

public class FileUploadHolder {
    public static String fileName;
    public static File recievedFile;
    public static BufferedOutputStream fileOutPutStream;
    public static long totalSize;


    public BufferedOutputStream getFileOutPutStream() {
        return fileOutPutStream;
    }

    public void setFileName(String fileName) {
        Timber.d("leafage-" + "setFileName");
        this.fileName = fileName;
        totalSize = 0;
        if (!Constants.DIR.exists()) {
            Constants.DIR.mkdirs();
        }
        this.recievedFile = new File(Constants.DIR, this.fileName);
        Timber.d(recievedFile.getAbsolutePath());
        try {
            fileOutPutStream = new BufferedOutputStream(new FileOutputStream(recievedFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        if (fileOutPutStream != null) {
            try {
                fileOutPutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileOutPutStream = null;
    }

    public void write(byte[] data) {
        if (fileOutPutStream != null) {
            try {
                fileOutPutStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        totalSize += data.length;
    }
}

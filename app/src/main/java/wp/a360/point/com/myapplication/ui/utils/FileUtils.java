package wp.a360.point.com.myapplication.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DN on 2017/8/7.
 */

public class FileUtils {
    private final static String TEMP = ".tmp";
    private static String DEFAULT_FILE_DIR;//默认下载目录

    public static File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    /**
     * 默认下载目录
     *
     * @return
     */
    public static String getDefaultDirectory(String apkPath) {
        try {
            if (TextUtils.isEmpty(apkPath)) {
                DEFAULT_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "360wallpaper";
                File file = new File(DEFAULT_FILE_DIR);
                if (!file.exists()) {
                    file.mkdirs();
                }
                DEFAULT_FILE_DIR = DEFAULT_FILE_DIR + File.separator;
            } else {
                return apkPath;
            }
            return DEFAULT_FILE_DIR;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }


    }

    /**
     * 默认下载文件的名称
     *
     * @return
     */
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获取APP内置存储下的文件目录
     *
     * @return 文件内置存储根路径
     */
    public static String getDiskCacheRootDir(Context context) {
        File diskRootFile = context.getFilesDir();
        String cachePath = null;
        if (diskRootFile != null) {
            cachePath = diskRootFile.getPath();
        } else {
            throw new IllegalArgumentException("disk is invalid");
        }
        return cachePath;
    }

    /**
     * 获取sdcard目录
     *
     * @return sdcard目录根路径
     */
    public static String getAbsolutePath() {
        String absolutePath = null;
        try {
            absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Exception e) {
            throw new IllegalArgumentException("disk is invalid");
        }
        return absolutePath;
    }

    //判断文件是否存在
    public static boolean exists(String fileName) {
        if (fileName == null) {
            return false;
        }
        File file = new File(fileName);
        return file.exists();
    }

    //3. 判断SDCard的文件大小不小于指定的
    public static boolean fileIsFull(String path, double size) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            int available = inputStream.available();
            if (available >= size) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 文件重命名。
     *
     * @param oldFile 旧文件
     * @return File 新文件
     */
    public static File rename(File oldFile) {
        String oldName = oldFile.getAbsolutePath();
        if (oldName.contains(TEMP)) {
            int index = oldName.lastIndexOf(TEMP);
            String newName = oldName.subSequence(0, index).toString();
            File file = new File(newName);
            if (!file.exists()) {
                oldFile.renameTo(file);// 重命名
            }
        }
        return oldFile;
    }

    /**
     * //判断图片是否完整
     *
     * @param filePath
     * @return
     */
    public static boolean isIntact(String filePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath.toString());
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
                return true;
            }
            System.gc();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * @param file
     * @return
     */
    public static void cleanImage(File file) throws Exception {
        if (file.exists()) {  //文件夹是否存在
            File[] list = file.listFiles();
            for (File newFile : list) {
                if (newFile.isDirectory()) {
                    //是的话，返回步骤B
                    cleanImage(file);
                } else {
                    newFile.delete();
                }
            }

        }
    }

    /**
     * 删除文件
     *
     * @param imageUrl
     * @return
     */
    public static boolean cleanFile(String imageUrl) {
        try {
            String defaultDirectory = getDefaultDirectory(null);
            String fileName = getFileName(imageUrl);
            File file = new File(defaultDirectory + fileName);
            if (file.exists()) {
                file.delete();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean downloadImage(String downloadUrl, String savePath) {
        HttpURLConnection connection = null;
        FileOutputStream output = null;
        InputStream input = null;
        URL urlObj = null;
        try {
            // 判断sdCard是否存在
            if (isSupportSdCard()) {
                if (urlObj == null) {
                    urlObj = new URL(downloadUrl);
                }
                connection = (HttpURLConnection) urlObj.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                String responseMsg = connection.getResponseMessage();
                output = new FileOutputStream(savePath);
                byte[] buffer = new byte[1024];
                int len = 0;
                //long current = 0L;
                //long total = connection.getContentLength();
                //int progress = 0;
                if ("OK".equalsIgnoreCase(responseMsg)) {
                    input = connection.getInputStream();
                    while ((len = input.read(buffer)) > 0) {
                        output.write(buffer, 0, len);
                    }
                    input.close();
                    output.flush();
                    output.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }


    /**
     * 判断sdcard是否存在
     * @return
     */
    public static boolean isSupportSdCard() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception ex) {
            return false;
        }
    }


}

package com.ssrs.qiniuyun.util;

import cn.hutool.core.io.FileUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import com.ssrs.qiniuyun.config.QiniuPublicBucket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 七牛云存储工具类
 *
 * @author ssrs
 */
public class QiniuUtil {

    private static int LIMIT_SIZE = 1000;

    /**
     * 返回七牛帐号的所有空间
     *
     * @return String[]
     * @throws QiniuException
     */
    public static String[] listBucket() throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        return bucketManager.buckets();
    }


    /**
     * 获取指定空间下的文件列表
     *
     * @param bucketName 空间名称
     * @param prefix     文件名前缀
     * @param limit      每次迭代的长度限制，最大1000，推荐值 100[即一个批次从七牛拉多少条]
     * @return List<FileInfo>
     */
    public static List<FileInfo> listFileOfBucket(String bucketName, String prefix, int limit) {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        BucketManager.FileListIterator it = bucketManager.createFileListIterator(bucketName, prefix, limit, null);
        List<FileInfo> list = new ArrayList<FileInfo>();
        while (it.hasNext()) {
            FileInfo[] items = it.next();
            if (null != items && items.length > 0) {
                list.addAll(Arrays.asList(items));
            }
        }
        return list;
    }


    /**
     * 七牛文件上传
     *
     * @param byteData   待上传字节
     * @param bucketName 空间名称
     * @param key        文件key
     * @param mimeType   文件类型
     * @return String
     * @throws IOException
     */
    public static String uploadFile(byte[] byteData, String bucketName, String key, String mimeType) throws IOException {
        Auth auth = QiniuConfigUtil.getQiniuAuth();
        String token = auth.uploadToken(bucketName);
        Response response = QiniuConfigUtil.getQiniuUploadManager().put(byteData, key, token, null, mimeType, false);
        return response.bodyString();
    }


    /**
     * 七牛文件上传
     *
     * @param byteData   待上传字节
     * @param bucketName 空间名称
     * @param key        文件key
     * @return String
     * @throws IOException
     */
    public static String uploadFile(byte[] byteData, String bucketName, String key) throws IOException {
        return uploadFile(byteData, bucketName, key, null);
    }


    /**
     * 七牛文件上传
     *
     * @param byteData 待上传文件输入流
     * @param key      文件key
     * @return String
     * @throws IOException
     */
    public static String uploadFile(byte[] byteData, String key) throws IOException {
        return uploadFile(byteData, QiniuPublicBucket.getValue(), key);
    }


    /**
     * 七牛文件上传
     *
     * @param inputStream 待上传文件输入流
     * @param bucketName  空间名称
     * @param key         空间内文件的key
     * @param mimeType    可件的MIME类型，可选参数，不传入会自动判断
     * @return
     * @throws IOException
     */
    public static String uploadFile(InputStream inputStream, String bucketName, String key, String mimeType) throws IOException {
        Auth auth = QiniuConfigUtil.getQiniuAuth();
        String token = auth.uploadToken(bucketName);
        byte[] byteData = IOUtils.toByteArray(inputStream);
        Response response = QiniuConfigUtil.getQiniuUploadManager().put(byteData, key, token, null, mimeType, false);
        inputStream.close();
        return response.bodyString();
    }


    /**
     * 七牛文件上传
     *
     * @param inputStream 待上传文件输入流
     * @param bucketName  空间名称
     * @param key         空间内文件的key
     * @return
     * @throws IOException
     */
    public static String uploadFile(InputStream inputStream, String bucketName, String key) throws IOException {
        return uploadFile(inputStream, bucketName, key, null);
    }


    /**
     * 七牛文件上传
     *
     * @param inputStream 待上传文件输入流
     * @param key         空间名称
     * @return
     * @throws IOException
     */
    public static String uploadFile(InputStream inputStream, String key) throws IOException {
        return uploadFile(inputStream, QiniuPublicBucket.getValue(), key);
    }


    /**
     * 七牛文件上传
     *
     * @param file       待上传文件
     * @param bucketName 空间名称
     * @param key        空间内文件的key
     * @param mimeType   可件的MIME类型，可选参数，不传入会自动判断
     * @return
     * @throws IOException
     */
    public static String uploadFile(File file, String bucketName, String key, String mimeType) throws IOException {
        String fileName = FileUtil.getName(file);
        Auth auth = QiniuConfigUtil.getQiniuAuth();
        String token = auth.uploadToken(bucketName);
        InputStream is = new FileInputStream(file);
        byte[] byteData = IOUtils.toByteArray(is);
        Response response = QiniuConfigUtil.getQiniuUploadManager().put(byteData, (key == null || "".equals(key)) ? fileName : key, token, null, mimeType, false);
        is.close();
        return response.bodyString();
    }

    /**
     * 七牛文件上传
     *
     * @param file       待上传文件
     * @param bucketName 空间名称
     * @param key        空间内文件的key
     * @return
     * @throws IOException
     */
    public static String uploadFile(File file, String bucketName, String key) throws IOException {
        return uploadFile(file, bucketName, key, null);
    }

    /**
     * 七牛文件上传
     *
     * @param file 待上传文件
     * @param key  空间内文件的key
     * @return
     * @throws IOException
     */
    public static String uploadFile(File file, String key) throws IOException {
        return uploadFile(file, QiniuPublicBucket.getValue(), key, null);
    }

    /**
     * 七牛文件上传
     *
     * @param file 待上传文件
     * @return
     * @throws IOException
     */
    public static String uploadFile(File file) throws IOException {
        return uploadFile(file, QiniuPublicBucket.getValue(), FileUtil.getName(file), null);
    }


    /**
     * 七牛文件上传
     *
     * @param filePath   待上传文件的硬盘路径
     * @param bucketName 空间名称
     * @param key        空间内文件的key
     * @param mimeType   可件的MIME类型，可选参数，不传入会自动判断
     * @return
     * @throws IOException
     */
    public static String uploadFile(String filePath, String bucketName, String key, String mimeType) throws IOException {
        Auth auth = QiniuConfigUtil.getQiniuAuth();
        String token = auth.uploadToken(bucketName);
        File file = new File(filePath);
        InputStream is = new FileInputStream(file);
        byte[] byteData = IOUtils.toByteArray(is);
        Response response = QiniuConfigUtil.getQiniuUploadManager().put(byteData, (key == null || "".equals(key)) ? FileUtil.getName(file) : key, token, null, mimeType, false);
        is.close();
        return response.bodyString();
    }

    /**
     * 七牛文件上传
     *
     * @param filePath   待上传文件的硬盘路径
     * @param bucketName 空间名称
     * @param key        空间内文件的key
     * @return
     * @throws IOException
     */
    public static String uploadFile(String filePath, String bucketName, String key) throws IOException {
        return uploadFile(filePath, bucketName, key, null);
    }

    /**
     * 七牛文件上传
     *
     * @param filePath 待上传文件的硬盘路径
     * @param key      空间内文件的key
     * @return
     * @throws IOException
     */
    public static String uploadFile(String filePath, String key) throws IOException {
        return uploadFile(filePath, QiniuPublicBucket.getValue(), key, null);
    }

    /**
     * 七牛文件上传
     *
     * @param filePath 待上传文件的硬盘路径
     * @return
     * @throws IOException
     */
    public static String uploadFile(String filePath) throws IOException {
        return uploadFile(filePath, QiniuPublicBucket.getValue(), FileUtil.getName(filePath), null);
    }


    /**
     * 提取网络资源并上传到七牛空间里, 不指定key，则默认使用url作为文件的key
     *
     * @param url        网络上一个资源文件的URL
     * @param bucketName 空间名称
     * @param key        空间内文件的key[唯一的]
     * @return
     * @throws QiniuException
     */
    public static String fetchToBucket(String url, String bucketName, String key) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FetchRet putret = bucketManager.fetch(url, bucketName, key);
        return putret.key;
    }


    /**
     * 提取网络资源并上传到七牛空间里, 不指定key，则默认使用url作为文件的key
     *
     * @param url
     * @param bucketName
     * @return
     * @throws QiniuException
     */
    public static String fetchToBucket(String url, String bucketName) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FetchRet putret = bucketManager.fetch(url, bucketName);
        return putret.key;
    }


    /**
     * 七牛空间内文件复制
     *
     * @param bucket       源空间名称
     * @param key          源空间里文件的key(唯一的)
     * @param targetBucket 目标空间
     * @param targetKey    目标空间里文件的key(唯一的)
     * @throws QiniuException
     */
    public static void copyFile(String bucket, String key, String targetBucket, String targetKey) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        bucketManager.copy(bucket, key, targetBucket, targetKey);
    }


    /**
     * 七牛空间内文件剪切
     *
     * @param bucket       源空间名称
     * @param key          源空间里文件的key(唯一的)
     * @param targetBucket 目标空间
     * @param targetKey    目标空间里文件的key(唯一的)
     * @throws QiniuException
     */
    public static void moveFile(String bucket, String key, String targetBucket, String targetKey) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        bucketManager.move(bucket, key, targetBucket, targetKey);
    }


    /**
     * 七牛空间内文件重命名
     *
     * @param bucket
     * @param key
     * @param targetKey
     * @throws QiniuException
     */
    public static void renameFile(String bucket, String key, String targetKey) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        bucketManager.rename(bucket, key, targetKey);
    }

    /**
     * 七牛空间内文件删除
     *
     * @param bucket 空间名称
     * @param key    空间内文件的key[唯一的]
     * @throws QiniuException
     */
    public static void deleteFile(String bucket, String key) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        bucketManager.delete(bucket, key);
    }


    /**
     * 返回指定空间下的所有文件信息
     *
     * @param bucketName 空间名称
     * @param prefix     文件key的前缀
     * @param limit      批量提取的最大数目
     * @return
     * @throws QiniuException
     */
    public static FileInfo[] findFiles(String bucketName, String prefix, int limit) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FileListing listing = bucketManager.listFiles(bucketName, prefix, null, limit, null);
        if (listing == null || listing.items == null || listing.items.length <= 0) {
            return null;
        }
        return listing.items;
    }

    /**
     * 返回指定空间下的所有文件信息
     *
     * @param bucketName 空间名称
     * @param prefix     文件key的前缀
     * @return
     * @throws QiniuException
     */
    public static FileInfo[] findFiles(String bucketName, String prefix) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FileListing listing = bucketManager.listFiles(bucketName, prefix, null, LIMIT_SIZE, null);
        if (listing == null || listing.items == null || listing.items.length <= 0) {
            return null;
        }
        return listing.items;
    }

    /**
     * 返回指定空间下的所有文件信息
     *
     * @param bucketName
     * @return
     * @throws QiniuException
     */
    public static FileInfo[] findFiles(String bucketName) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FileListing listing = bucketManager.listFiles(bucketName, null, null, LIMIT_SIZE, null);
        if (listing == null || listing.items == null || listing.items.length <= 0) {
            return null;
        }
        return listing.items;
    }

    /**
     * 返回指定空间下的某个文件
     *
     * @param bucketName
     * @param key
     * @param limit
     * @return
     * @throws QiniuException
     */
    public static FileInfo findOneFile(String bucketName, String key, int limit) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FileListing listing = bucketManager.listFiles(bucketName, key, null, limit, null);
        if (listing == null || listing.items == null || listing.items.length <= 0) {
            return null;
        }
        return (listing.items)[0];
    }

    /**
     * 返回指定空间下的某个文件(重载)
     *
     * @param bucketName
     * @param key
     * @return
     * @throws QiniuException
     */
    public static FileInfo findOneFile(String bucketName, String key) throws QiniuException {
        BucketManager bucketManager = QiniuConfigUtil.getQiniuBucketManager();
        FileListing listing = bucketManager.listFiles(bucketName, key, null, LIMIT_SIZE, null);
        if (listing == null || listing.items == null || listing.items.length <= 0) {
            return null;
        }
        return (listing.items)[0];
    }


}

package com.ssrs.fastdfs.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ssrs.fastdfs.config.FdfsGroupNameConf;
import com.ssrs.fastdfs.config.FdfsUrlConf;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * fastdfs客户端上传工具
 *
 * @author ssrs
 */
public class FastDFSClient {
    private static Log logger = LogFactory.get(FastDFSClient.class);

    public static String[] upload(FastDFSFile file) {
        logger.info("File Name: " + file.getName() + "File Length:" + file.getContent().length);

        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", file.getAuthor());

        long startTime = System.currentTimeMillis();
        String[] uploadResults = null;
        StorageClient storageClient = null;
        try {
            storageClient = getTrackerClient();
            uploadResults = storageClient.uploadFile(file.getGroupName(), file.getContent(), file.getExt(), meta_list);
        } catch (IOException e) {
            logger.error("IO Exception when uploadind the file:" + file.getName(), e);
        } catch (Exception e) {
            logger.error("Non IO Exception when uploadind the file:" + file.getName(), e);
        }
        logger.info("upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");

        if (uploadResults == null && storageClient != null) {
            logger.error("upload file fail, error code:" + storageClient.getErrorCode());
        }
        String groupName = uploadResults[0];
        String remoteFileName = uploadResults[1];

        logger.info("upload file successfully!!!" + "group_name:" + groupName + ", remoteFileName:" + " " + remoteFileName);
        return uploadResults;
    }

    /***
     * 根据文件路径上传
     * @param resultFile
     * @return
     * @throws IOException
     */
    public static String[] upload(String resultFile) throws IOException {
        String[] fileAbsolutePath = {};
        File baseFile = new File(resultFile);
        String ext = resultFile.substring(resultFile.lastIndexOf(".") + 1);
        if (resultFile.indexOf(".") <= 0) {
            ext = null;
        }

        byte[] file_buff = null;
        FileInputStream inputStream = new FileInputStream(baseFile);
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(resultFile, file_buff, ext);
        try {
            fileAbsolutePath = upload(file);  //upload to fastdfs
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fileAbsolutePath == null) {
        }
        return fileAbsolutePath;
    }

    /***
     * 根据文件路径上传
     * @param resultFile
     * @return
     * @throws IOException
     */
    public static String[] upload(String resultFile, String ext) throws IOException {
        String[] fileAbsolutePath = {};
        File baseFile = new File(resultFile);
        byte[] file_buff = null;
        FileInputStream inputStream = new FileInputStream(baseFile);
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(resultFile, file_buff, ext);
        try {
            fileAbsolutePath = upload(file);  //upload to fastdfs
        } catch (Exception e) {

        }
        if (fileAbsolutePath == null) {
        }
        return fileAbsolutePath;
    }

    /***
     * 根据文件路径上传
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public static String[] upload(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = upload(file);  //upload to fastdfs
        } catch (Exception e) {
            logger.error("upload file Exception!", e);
        }
        if (fileAbsolutePath == null) {
            logger.error("upload file failed,please upload again!");
        }
        return fileAbsolutePath;
    }

    /***
     * 根据文件路径上传
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public static String[] upload(MultipartFile multipartFile, String ext) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = upload(file);  //upload to fastdfs
        } catch (Exception e) {
            logger.error("upload file Exception!", e);
        }
        if (fileAbsolutePath == null) {
            logger.error("upload file failed,please upload again!");
        }
        return fileAbsolutePath;
    }

    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getTrackerClient();
            return storageClient.getFileInfo(groupName, remoteFileName);
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    public static void fileDownload(String readFileName, String remoteFileName) throws IOException {
        InputStream is = FastDFSClient.downFile(FdfsGroupNameConf.getValue(), remoteFileName);
        // 读入文件存在判断
        if (is == null) {
            return;
        }
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        File targetFile = new File(readFileName);
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
        outStream.close();
        is.close();
    }

    public static InputStream downFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getTrackerClient();
            byte[] fileByte = storageClient.downloadFile(groupName, remoteFileName);
            InputStream ins = new ByteArrayInputStream(fileByte);
            return ins;
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    public static void deleteFile(String groupName, String remoteFileName)
            throws Exception {
        StorageClient storageClient = getTrackerClient();
        int i = storageClient.deleteFile(groupName, remoteFileName);
        logger.info("delete file successfully!!!" + i);
    }

    public static StorageServer[] getStoreStorages(String groupName)
            throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getStoreStorages(trackerServer, groupName);
    }

    public static ServerInfo[] getFetchStorages(String groupName,
                                                String remoteFileName) throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    public static String getTrackerUrl() throws IOException {
        //return "http://"+getTrackerServer().getInetSocketAddress().getHostString()+":"+ClientGlobal.getTrackerHttpPort()+"/";
        if (StrUtil.endWith(FdfsUrlConf.getValue(), "/")) {
            return FdfsUrlConf.getValue();
        } else {
            return FdfsUrlConf.getValue() + "/";
        }

    }

    private static StorageClient getTrackerClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }
}
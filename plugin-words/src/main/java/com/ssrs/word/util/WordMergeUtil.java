package com.ssrs.word.util;

import org.apache.commons.io.IOUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用docx4j合并，doc格式不支持
 *
 * @Author yaohong
 * @Date 2020/8/15
 * @Description
 */
public class WordMergeUtil {

    /**
     * 合并word文件
     *
     * @param wordList 文件路径
     * @param out      输出流（需要调用方手动关闭）
     * @throws IOException
     * @throws Docx4JException
     */
    public static void mergeDoc(List<String> wordList, OutputStream out) throws IOException, Docx4JException {
        List<InputStream> streamList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wordList)) {
            throw new RuntimeException("wordList must not empty!");
        }
        for (String wordPath : wordList) {
            streamList.add(new FileInputStream(wordPath));
        }
        mergeDocStream(streamList, out);
    }


    /**
     * @param streamList word文件流
     * @param out        输出流（需要调用方手动关闭）
     * @throws Docx4JException
     * @throws IOException
     */
    public static void mergeDocStream(List<InputStream> streamList, OutputStream out) throws Docx4JException, IOException {
        WordprocessingMLPackage target = null;
        final File generated = File.createTempFile("generated", ".docx");
        int chunkId = 0;

        for (InputStream is : streamList) {
            if (is != null) {
                if (target == null) {
                    OutputStream os = new FileOutputStream(generated);
                    os.write(IOUtils.toByteArray(is));
                    os.close();
                    target = WordprocessingMLPackage.load(generated);
                } else {
                    insertDoc(target.getMainDocumentPart(), IOUtils.toByteArray(is), chunkId++);
                }
            }
        }
        if (target != null) {
            target.save(generated);
            FileInputStream fileInputStream = new FileInputStream(generated);
            saveTemplate(fileInputStream, out);
        }
    }

    /**
     * 插入一个文档
     *
     * @param mainDocumentPart
     * @param bytes
     * @param chunkId
     * @throws InvalidFormatException
     */
    public static void insertDoc(MainDocumentPart mainDocumentPart, byte[] bytes, int chunkId) throws InvalidFormatException {
        PartName partName = new PartName("/part" + chunkId + ".docx");
        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(partName);
        afiPart.setBinaryData(bytes);
        Relationship relationship = mainDocumentPart.addTargetPart(afiPart);
        CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
        chunk.setId(relationship.getId());
        mainDocumentPart.addObject(chunk);
    }

    /**
     * 保存文档
     *
     * @param targetStream
     * @param out
     * @throws IOException
     */
    public static void saveTemplate(InputStream targetStream, OutputStream out) throws IOException {
        FileOutputStream fos;
        int bytesum = 0;
        int byteread = 0;

        //fos = new FileOutputStream(targetWordPath);
        fos = (FileOutputStream) out;
        byte[] buffer = new byte[1024];
        while ((byteread = targetStream.read(buffer)) != -1) {
            bytesum += byteread; // 字节数 文件大小
            fos.write(buffer, 0, byteread);
        }
        targetStream.close();
        fos.close();

    }
}

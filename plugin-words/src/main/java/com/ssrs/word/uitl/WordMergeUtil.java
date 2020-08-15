package com.ssrs.word.uitl;

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
import java.util.Iterator;
import java.util.List;

/**
 * @Author yaohong
 * @Date 2020/8/15
 * @Description
 */
public class WordMergeUtil {
    public static void mergeDoc(List<String> wordList, OutputStream out) throws IOException, Docx4JException {
        List<InputStream> streamList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wordList)) {
            // 这里可以抛出一个异常
        }
        for (String wordPath : wordList) {
            streamList.add(new FileInputStream(wordPath));
        }
        mergeDocStream(streamList, out);
    }


    private static void mergeDocStream(List<InputStream> streamList, OutputStream out) throws Docx4JException, IOException {
        WordprocessingMLPackage target = null;
        final File generated = File.createTempFile("generated", ".docx");
        int chunkId = 0;
        Iterator<InputStream> iterator = streamList.iterator();

        while (iterator.hasNext()) {
            InputStream is = iterator.next();
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

    private static void insertDoc(MainDocumentPart mainDocumentPart, byte[] bytes, int chunkId) throws InvalidFormatException {
        PartName partName = new PartName("/part" + chunkId + ".docx");
        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(partName);
        afiPart.setBinaryData(bytes);
        Relationship relationship = mainDocumentPart.addTargetPart(afiPart);
        CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
        chunk.setId(relationship.getId());
        mainDocumentPart.addObject(chunk);
    }

    private static void saveTemplate(InputStream targetStream, OutputStream out) throws IOException {
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

//    public static void main(String[] args) {
//        try {
//            String filePath1 = "plugin-words/src/main/resources/测试文件1.docx";
//            String filePath2 = "plugin-words/src/main/resources/测试文件2.docx";
//            String filePath3 = "plugin-words/src/main/resources/测试文件3.docx";
//            List<String> list = new ArrayList<>();
//            list.add(filePath1);
//            list.add(filePath2);
//            list.add(filePath3);
//            // 输出文件的路径
//            File file = new File("plugin-words/src/main/resources/合并文件.docx");
//            mergeDoc(list, new FileOutputStream(file));
//            System.out.println("合并完成");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

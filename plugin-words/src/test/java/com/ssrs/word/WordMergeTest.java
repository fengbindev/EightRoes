package com.ssrs.word;

import com.ssrs.word.util.WordMergeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ssrs
 */
public class WordMergeTest {

    public static void main(String[] args) {
        try {
            String filePath1 = "plugin-words/src/main/resources/测试文件1.docx";
            String filePath2 = "plugin-words/src/main/resources/测试文件2.docx";
            String filePath3 = "plugin-words/src/main/resources/测试文件3.docx";
            List<String> list = new ArrayList<>();
            list.add(filePath1);
            list.add(filePath2);
            list.add(filePath3);
            // 输出文件的路径
            File file = new File("plugin-words/src/main/resources/合并文件.docx");
            WordMergeUtil.mergeDoc(list, new FileOutputStream(file));
            System.out.println("合并完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

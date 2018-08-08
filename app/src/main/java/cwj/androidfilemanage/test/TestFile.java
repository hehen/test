package cwj.androidfilemanage.test;

import java.io.File;
import java.util.List;

import cwj.androidfilemanage.bean.FileInfo;

import static cwj.androidfilemanage.utils.FileUtil.fileFilter;
import static cwj.androidfilemanage.utils.FileUtil.getFileInfosFromFileArray;

/**
 * @author wenlu
 * @desc
 * @date 2018/8/8 15:10
 */
public class TestFile {
    public static void main(String[] args) {
        File[] files = fileFilter(new File("D:"));
        List<FileInfo> fileInfos = getFileInfosFromFileArray(files);
        for (FileInfo fileinfo :fileInfos) {
            System.out.println(fileinfo.toString());
        }
    }
}

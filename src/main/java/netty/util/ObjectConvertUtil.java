package netty.util;


import netty.model.RequestFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchen
 * @date 2018/3/28 14:45
 */
public class ObjectConvertUtil {
    public static Map<String, RequestFile> convert(File[] files) throws Exception {

        Map<String, RequestFile> requestFiles = new ConcurrentHashMap<>();

        if (files != null && files.length > 0) {

            for (int i = 0; i < files.length; i++) {

                File file = files[i];
                RequestFile requestFile = new RequestFile();
                requestFile.setFile(file);
                requestFile.setFileName(file.getName());
                requestFile.setFileMd5(MD5FileUtil.getMD5String(file));
                requestFile.setFileType(new MimetypesFileTypeMap().getContentType(file));
                requestFile.setStartPosition(0);

                requestFiles.put(file.getName(), requestFile);
            }

        }

        return requestFiles;
    }
}

package netty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(MD5FileUtil.class);

    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',  '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 对文件进行MD5加密
     */
    public static String getMD5String(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest());
    }

    /**
     * 对字符串进行MD5加密
     */
    public static String getMD5String(String s) throws NoSuchAlgorithmException {
        return getMD5String(s.getBytes());
    }

    /**
     * 对byte类型的数组进行MD5加密
     */
    public static String getMD5String(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            char c0 = hexDigits[(bytes[l] & 0xf0) >> 4];
            char c1 = hexDigits[bytes[l] & 0xf];
            stringbuffer.append(c0);
            stringbuffer.append(c1);
        }
        return stringbuffer.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String transfer = MD5FileUtil.getMD5String("Transfer");
        System.out.println(transfer);
    }
}

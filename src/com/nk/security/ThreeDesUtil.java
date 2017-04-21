package com.nk.security;

import com.nk.excel.util.FileUtil;
import org.apache.commons.lang.StringEscapeUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class ThreeDesUtil {

    private static final int MAX_MSG_LENGTH = 300 * 1024 * 1024; //报文最大长度300M
    private static final String DES_KEY = "1GE45D7J9C1N3Y5F78A0F23F56F8C0AB";//秘钥32位

    private static final String Algorithm = "DESede"; //定义加密算法,可用 DES,DESede,Blowfish

    //    private final static String PADDING = "DESede/ECB/PKCS5Padding";
    private final static String PADDING = "DESede/ECB/NoPadding"; //填充方式为不填充
    public static final byte[] DEFAULT_KEY = {
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30,
            0x31, 0x32, 0x33, 0x34
    };  //24字节的密钥

    /**
     * 3DES 加密
     *
     * @param keybyte 加密密钥，长度为24字节
     * @param src     被加密的数据缓冲区（源）
     * @return
     */
    public static byte[] encrypt(byte[] keybyte, byte[] src) {
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            //加密
            Cipher c1 = Cipher.getInstance(PADDING);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);//在单一方面的加密或解密
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 3DES 解密
     *
     * @param keybyte 为加密密钥，长度为24字节
     * @param src     为加密后的缓冲区
     * @return
     */
    private static byte[] decrypt(byte[] keybyte, byte[] src) {
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            //解密
            Cipher c1 = Cipher.getInstance(PADDING);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    //转换成十六进制字符串
    private static String byte2Hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) hs = hs + ":";
        }
        return hs.toUpperCase();
    }

    public static String encrypt2HexStr(String sourceData) {
        byte[] key = BASE64.decode(DES_KEY);
        return encrypt2HexStr(key, sourceData);
    }

    /**
     * 将元数据进行补位后进行3DES加密
     * <p/>
     * 补位后 byte[] = 描述有效数据长度(int)的byte[]+原始数据byte[]+补位byte[]
     *
     * @param sourceData 元数据字符串
     * @return 返回3DES加密后的16进制表示的字符串
     */
    public static String encrypt2HexStr(byte[] keys, String sourceData) {
        byte[] source = new byte[0];
        try {
            //元数据
            sourceData = StringEscapeUtils.escapeJavaScript(sourceData);
            source = sourceData.getBytes("UTF-8");

            //1.原数据byte长度
            int merchantData = source.length;
//            System.out.println("原数据据:" + sourceData);
//            System.out.println("原数据byte长度:" + merchantData);
//            System.out.println("原数据HEX表示:" + bytes2Hex(source));
            //2.计算补位
            int x = (merchantData + 4) % 8;
            int y = (x == 0) ? 0 : (8 - x);
//            System.out.println("需要补位 :" + y);
            //3.将有效数据长度byte[]添加到原始byte数组的头部
            byte[] sizeByte = intToByteArray(merchantData);
            byte[] resultByte = new byte[merchantData + 4 + y];
            resultByte[0] = sizeByte[0];
            resultByte[1] = sizeByte[1];
            resultByte[2] = sizeByte[2];
            resultByte[3] = sizeByte[3];

            //4.填充补位数据
            for (int i = 0; i < merchantData; i++) {
                resultByte[4 + i] = source[i];
            }
            for (int i = 0; i < y; i++) {
                resultByte[merchantData + 4 + i] = 0x00;
            }
//            System.out.println("补位后的byte数组长度:" + resultByte.length);
//            System.out.println("补位后数据HEX表示:" + bytes2Hex(resultByte));
//            System.out.println("秘钥HEX表示:" + bytes2Hex(keys));
//            System.out.println("秘钥长度:" + keys.length);

            byte[] desdata = ThreeDesUtil.encrypt(keys, resultByte);

//            System.out.println("加密后的长度:" + desdata.length);

            return bytes2Hex(desdata);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt2HexStr(byte[] keys, byte[] source) {
        //1.原数据byte长度
        int merchantData = source.length;
        //2.计算补位
        int x = (merchantData + 4) % 8;
        int y = (x == 0) ? 0 : (8 - x);
        //3.将有效数据长度byte[]添加到原始byte数组的头部
        byte[] sizeByte = intToByteArray(merchantData);
        byte[] resultByte = new byte[merchantData + 4 + y];
        resultByte[0] = sizeByte[0];
        resultByte[1] = sizeByte[1];
        resultByte[2] = sizeByte[2];
        resultByte[3] = sizeByte[3];

        //4.填充补位数据
        for (int i = 0; i < merchantData; i++) {
            resultByte[4 + i] = source[i];
        }
        for (int i = 0; i < y; i++) {
            resultByte[merchantData + 4 + i] = 0x00;
        }

        byte[] desdata = ThreeDesUtil.encrypt(keys, resultByte);

        return bytes2Hex(desdata);
    }

    /**
     * 3DES 解密 进行了补位的16进制表示的字符串数据
     *
     * @return
     */
    public static String decrypt4HexStr(byte[] keys, String data) {

        byte[] hexSourceData = new byte[0];
        try {
            hexSourceData = hex2byte(data.getBytes("UTF-8"));
            // 解密
            byte[] unDesResult = ThreeDesUtil.decrypt(keys, hexSourceData);
            // byte数组前4位为原始报文长度
            byte[] dataSizeByte = new byte[4];
            dataSizeByte[0] = unDesResult[0];
            dataSizeByte[1] = unDesResult[1];
            dataSizeByte[2] = unDesResult[2];
            dataSizeByte[3] = unDesResult[3];
            //有效数据长度
            int dsb = byteArrayToInt(dataSizeByte, 0);
            if (dsb > MAX_MSG_LENGTH) {
                throw new RuntimeException("msg over MAX_MSG_LENGTH or msg error");
            }

            byte[] tempData = new byte[dsb];
            for (int i = 0; i < dsb; i++) {
                tempData[i] = unDesResult[4 + i];
            }

            return hex2bin(toHexString(tempData));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt4HexByte(byte[] keys, String data) {

        byte[] hexSourceData = new byte[0];
        try {
            hexSourceData = hex2byte(data.getBytes("UTF-8"));
            // 解密
            byte[] unDesResult = ThreeDesUtil.decrypt(keys, hexSourceData);
            // byte数组前4位为原始报文长度
            byte[] dataSizeByte = new byte[4];
            dataSizeByte[0] = unDesResult[0];
            dataSizeByte[1] = unDesResult[1];
            dataSizeByte[2] = unDesResult[2];
            dataSizeByte[3] = unDesResult[3];
            //有效数据长度
            int dsb = byteArrayToInt(dataSizeByte, 0);
            if (dsb > MAX_MSG_LENGTH) {
                throw new RuntimeException("msg over MAX_MSG_LENGTH or msg error");
            }

            byte[] tempData = new byte[dsb];
            for (int i = 0; i < dsb; i++) {
                tempData[i] = unDesResult[4 + i];
            }
            if (tempData != null)
                return tempData;

            return hex2binByte(toHexString(tempData));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String hex2bin(String hex) throws UnsupportedEncodingException {
        String digital = "0123456789abcdef";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }

        return new String(bytes,"UTF-8");
    }

    private static byte[] hex2binByte(String hex) throws UnsupportedEncodingException {
        String digital = "0123456789abcdef";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }

        return bytes;
    }

    /**
     * 将byte数组 转换为16进制表示的字符串
     *
     * @param ba
     * @return
     */
    private static String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }

    /**
     * byte数组 转16进制表示的字符串
     *
     * @param bts
     * @return
     */
    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }

    /**
     * 将int 转换为 byte 数组
     *
     * @param i
     * @return
     */
    private static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        //必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i  & 0xFF000000);
        //，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数
        // 之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为
        // float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
        // 虽然 result[0] = (byte)(i  & 0xFF000000); 这样不行，但是我们可以这样 result[0] = (byte)((i  & //0xFF000000) >>24);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 将byte数组 转换为int
     *
     * @param b
     * @param offset 位游方式
     * @return
     */
    private static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    /**
     * 加密文件
     * @param file
     * @return
     */
    public static String encrypt2HexFile(File file) {
        byte[] key = BASE64.decode(DES_KEY);
        if (file == null || !file.exists()){
            return null;
        }

        return ThreeDesUtil.encrypt2HexStr(key, FileUtil.readByte(file));
    }

    /**
     * 解密文件
     * @param targetFile
     * @param fileValue
     * @return
     */
    public static boolean decrypt4HexByte(String targetFile, String fileValue) {
        try {
            byte[] key = BASE64.decode(DES_KEY);
            byte[] fileByte = ThreeDesUtil.decrypt4HexByte(key, fileValue);
            FileUtil.writeByte(new File(targetFile),fileByte);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("3des decrypt fail!");
        }
    }


    public static void main(String[] args) throws UnsupportedEncodingException {

//        File file = new File("D:\\JLib\\javacv-bin\\javacpp.jar");
//        String mi = encrypt2HexFile(file);
//        decrypt4HexByte("D:\\JLib\\javacv-bin\\javacpp1.jar",mi);
        String test = "哈哈哈";
        byte[] key = BASE64.decode(DES_KEY);
        String aa = encrypt2HexStr(key,test);
        System.out.println(aa);
        System.out.println(new String(decrypt4HexStr(key, aa).getBytes("ISO8859-1"), "gb2312"));
    }

    public static String decrypt4HexByte(String sourceData) {
        byte[] key = BASE64.decode(DES_KEY);
        return decrypt4HexStr(key, sourceData);
    }
}

package com.nk.excel.util;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * 文件操作工具类
 * 
 */
public class FileUtil {
	
	
	private static String GlobalCharset = "utf-8";
	
	/**
	 * 将文件路径规则化，去掉其中多余的/和\，去掉可能造成文件信息泄漏的../
	 */
	public static String normalizePath(String path) {
		path = path.replace('\\', '/');
		path = StringUtil.replaceEx(path, "../", "/");
		path = StringUtil.replaceEx(path, "./", "/");
		if (path.endsWith("..")) {
			path = path.substring(0, path.length() - 2);
		}
		path = path.replaceAll("/+", "/");
		return path;
	}

	public static File normalizeFile(File f) {
		String path = f.getAbsolutePath();
		path = normalizePath(path);
		return new File(path);
	}

	/**
	 * 以全局编码将指定内容写入指定文件
	 */
	public static boolean writeText(String fileName, String content) {
		fileName = normalizePath(fileName);
		return writeText(fileName, content, GlobalCharset);
	}

	/**
	 * 以指定编码将指定内容写入指定文件
	 */
	public static boolean writeText(String fileName, String content, String encoding) {
		fileName = normalizePath(fileName);
		return writeText(fileName, content, encoding, false);
	}

	/**
	 * 以指定编码将指定内容写入指定文件，如果编码为UTF-8且bomFlag为true,则在文件头部加入3字节的BOM
	 */
	public static boolean writeText(String fileName, String content, String encoding, boolean bomFlag) {
		fileName = normalizePath(fileName);
		try {
			byte[] bs = content.getBytes(encoding);
			if (encoding.equalsIgnoreCase("UTF-8") && bomFlag) {
				bs = ArrayUtils.addAll(StringUtil.BOM, bs);
			}
			writeByte(fileName, bs);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private static Map<String,String> head2FileType = new HashMap<String,String>();
	static{
		head2FileType.put("FFD8FFE1", "jpg");
		head2FileType.put("89504E47", "png");
		head2FileType.put("47494638 ", "gif");
		head2FileType.put("49492A00", "tif");
		head2FileType.put("424D", "bmp");
		head2FileType.put("41433130", "dwg");
		head2FileType.put("38425053 ", "psd");
		head2FileType.put("7B5C727466", "rtf");
		head2FileType.put("3C3F786D6C", "xml");
		head2FileType.put("68746D6C3E ", "html");
		head2FileType.put("44656C69766572792D646174", "eml");
		head2FileType.put("CFAD12FEC5FD746F ", "dbx");
		head2FileType.put("2142444E", "pst");
		head2FileType.put("D0CF11E0", "xls/doc");
		head2FileType.put("5374616E64617264204A", "mdb");
		head2FileType.put("FF575043", "wpd");
		head2FileType.put("252150532D41646F6265", "eps/ps");
		head2FileType.put("255044462D312E", "pdf");
		head2FileType.put("E3828596", "pwl");
		head2FileType.put("504B0304", "zip");
		head2FileType.put("52617221", "rar");
		head2FileType.put("57415645", "wav");
		head2FileType.put("41564920", "avi");
		head2FileType.put("2E7261FD", "ram");
		head2FileType.put("2E524D46", "rm");
		head2FileType.put("000001BA", "mpg");
		head2FileType.put("000001B3", "mpg");
		head2FileType.put("6D6F6F76", "mov");
		head2FileType.put("3026B2758E66CF11", "asf");
		head2FileType.put("4D546864", "mid");
	}

	/**
	 * 获取文件内容(无法识别xlxs、docx等新版文档格式)
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String getTypeByStream(InputStream is) throws IOException {
		byte[] b = new byte[4];
		is.read(b, 0, b.length);
		String type = bytesToHexString(b).toUpperCase();
		if (StringUtils.isNotBlank(type)) {
			return head2FileType.get(type);
		}
		return type;
	}

	/**
	 * 根据File获取文件内容，如果无法获取文件头信息则返回文件名后缀
	 * @param file
	 * @return
     */
	public static String getTypeByFile(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		try {
			InputStream inputStream = new FileInputStream(file);
			String streamType = getTypeByStream(inputStream);
			String path = file.getAbsolutePath();
			String suffix = path != null && path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : null;
			if ("zip".equals(streamType)) {
				if (suffix!=null && (suffix.equals("docx")
					||suffix.equals("xlsx"))) {
					return "xls/doc";
				}
			}
			if (streamType == null && suffix != null) {
				return suffix;
			}
			return streamType;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * byte数组转换成16进制字符串
	 *
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 *
	 * @Description: 写入到文件
	 * @Title: writeToFile
	 * @param path
	 * @param data void
	 * @throws
	 * @date 2016年4月27日 下午4:41:04
	 */
	public static void writeToFile(String path,String data){
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(path,true));
			bufferedWriter.write(data);
			bufferedWriter.newLine();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 以二进制方式读取文件
	 */
	public static byte[] readByte(String fileName) {
		fileName = normalizePath(fileName);
		try {
			FileInputStream fis = new FileInputStream(fileName);
			byte[] r = new byte[fis.available()];
			fis.read(r);
			fis.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以二进制方式读取文件
	 */
	public static byte[] readByte(File f) {
		f = normalizeFile(f);
		try {

			FileInputStream fis = new FileInputStream(f);
			byte[] r = readByte(fis);
			fis.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取指定流，并转换为二进制数组
	 */
	public static byte[] readByte(InputStream is) {
		try {
			byte[] r = new byte[is.available()];
			is.read(r);
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将二进制数组写入指定文件
	 */
	public static boolean writeByte(String fileName, byte[] b) {
		fileName = normalizePath(fileName);
		try {
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName));
			fos.write(b);
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将二进制数组写入指定文件
	 */
	public static boolean writeByte(File f, byte[] b) {
		f = normalizeFile(f);
		try {
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			fos.write(b);
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 以全局编码读取指定文件中的文本
	 */
	public static String readText(File f) {
		f = normalizeFile(f);
		return readText(f, GlobalCharset);
	}

	/**
	 * 以指定编码读取指定文件中的文本
	 */
	public static String readText(File f, String encoding) {
		f = normalizeFile(f);
		try {
			InputStream is = new FileInputStream(f);
			String str = readText(is, encoding);
			is.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以指定编码读取流中的文本
	 */
	public static String readText(InputStream is, String encoding) {
		try {
			byte[] bs = readByte(is);
			if (encoding.equalsIgnoreCase("utf-8")) {// 如果是UTF8则要判断有没有BOM
				if (StringUtil.hexEncode(ArrayUtils.subarray(bs, 0, 3)).equals("efbbbf")) {// BOM标志
					bs = ArrayUtils.subarray(bs, 3, bs.length);
				}
			}
			return new String(bs, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以全局编码读取指定文件中的文本
	 */
	public static String readText(String fileName) {
		fileName = normalizePath(fileName);
		return readText(fileName, GlobalCharset);
	}

	/**
	 * 以指定编码读取指定文件中的文本
	 */
	public static String readText(String fileName, String encoding) {
		fileName = normalizePath(fileName);
		try {
			InputStream is = new FileInputStream(fileName);
			String str = readText(is, encoding);
			is.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 以全局编码读取指定URL中的文本
	 */
	public static String readURLText(String urlPath) {
		return readURLText(urlPath, GlobalCharset);
	}

	/**
	 * 以指定编码读取指定URL中的文本
	 */
	public static String readURLText(String urlPath, String encoding) {
		try {
			URL url = new URL(urlPath);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除文件，不管路径是文件还是文件夹，都删掉。<br>
	 * 删除文件夹时会自动删除子文件夹。
	 */
	public static boolean delete(String path) {
		path = normalizePath(path);
		File file = new File(path);
		return delete(file);
	}

	/**
	 * 删除文件，不管路径是文件还是文件夹，都删掉。<br>
	 * 删除文件夹时会自动删除子文件夹。
	 */
	public static boolean delete(File f) {
		f = normalizeFile(f);
		if (!f.exists()) {
			System.out.println("文件或文件夹不存在：" + f);
			return false;
		}
		if (f.isFile()) {
			System.out.println("删除："+f.getAbsolutePath());
			return f.delete();
		} else {
			return FileUtil.deleteDir(f);
		}
	}

	/**
	 * 删除文件夹及其子文件夹
	 */
	private static boolean deleteDir(File dir) {
		dir = normalizeFile(dir);
		try {
			System.out.println("删除："+dir);
			return deleteFromDir(dir) && dir.delete(); // 先删除完里面所有内容再删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * 创建文件夹
	 */
	public static boolean mkdir(String path) {
		path = normalizePath(path);
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return true;
	}

	/**
	 * 通配符方式删除指定目录下的文件或文件夹。<br>
	 * 文件名支持使用正则表达式（文件路径不支持正则表达式）
	 */
	public static boolean deleteEx(String fileName) {
		fileName = normalizePath(fileName);
		int index1 = fileName.lastIndexOf("\\");
		int index2 = fileName.lastIndexOf("/");
		index1 = index1 > index2 ? index1 : index2;
		String path = fileName.substring(0, index1);
		String name = fileName.substring(index1 + 1);
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (Pattern.matches(name, files[i].getName())) {
					System.out.println("删除："+files[i].getAbsolutePath());
					files[i].delete();
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 删除文件夹里面的所有文件,但不删除自己本身
	 */
	public static boolean deleteFromDir(String dirPath) {
		dirPath = normalizePath(dirPath);
		File file = new File(dirPath);
		return deleteFromDir(file);
	}

	/**
	 * 删除文件夹里面的所有文件和子文件夹,但不删除自己本身
	 * 
	 * @param
	 * @return
	 */
	public static boolean deleteFromDir(File dir) {
		dir = normalizeFile(dir);
		if (!dir.exists()) {
			System.out.println("文件夹不存在：" + dir);
			return false;
		}
		if (!dir.isDirectory()) {
			System.out.println(dir + "不是文件夹");
			return false;
		}
		File[] tempList = dir.listFiles();
		for (int i = 0; i < tempList.length; i++) {
			System.out.println("删除："+dir);
			if (!delete(tempList[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 从指定位置复制文件到另一个文件夹，复制时不符合filter条件的不复制
	 */
	public static boolean copy(String oldPath, String newPath, FileFilter filter) {
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		File oldFile = new File(oldPath);
		File[] oldFiles = oldFile.listFiles(filter);
		boolean flag = true;
		if (oldFiles != null) {
			for (int i = 0; i < oldFiles.length; i++) {
				if (!copy(oldFiles[i], newPath + "/" + oldFiles[i].getName())) {
					flag = false;
				}
			}
		}
		return flag;
	}

	/**
	 * 从指定位置复制文件到另一个文件夹
	 */
	public static boolean copy(String oldPath, String newPath) {
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		File oldFile = new File(oldPath);
		return copy(oldFile, newPath);
	}

	public static boolean copy(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		if (!oldFile.exists()) {
			System.out.println("文件或者文件夹不存在：" + oldFile);
			return false;
		}
		if (oldFile.isFile()) {
			return copyFile(oldFile, newPath);
		} else {
			return copyDir(oldFile, newPath);
		}
	}

	/**
	 * 复制单个文件
	 */
	private static boolean copyFile(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		if (!oldFile.exists()) { // 文件存在时
			System.out.println("文件不存在：" + oldFile);
			return false;
		}
		if (!oldFile.isFile()) { // 文件存在时
			System.out.println(oldFile + "不是文件");
			return false;
		}
		if(oldFile.getName().equalsIgnoreCase("Thumbs.db")){
			System.out.println(oldFile + "忽略此文件");
			return true;
		}
		
		try {
			int byteread = 0;
			InputStream inStream = new FileInputStream(oldFile); // 读入原文件
			File newFile = new File(newPath);
			//如果新文件是一个目录，则创建新的File对象
			if(newFile.isDirectory()){
				newFile = new File(newPath,oldFile.getName());
			}
			FileOutputStream fs = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			fs.close();
			inStream.close();
		} catch (Exception e) {
			System.out.println("复制单个文件" + oldFile.getPath() + "操作出错。错误原因:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 复制整个文件夹内容
	 */
	private static boolean copyDir(File oldDir, String newPath) {
		oldDir = normalizeFile(oldDir);
		newPath = normalizePath(newPath);
		if (!oldDir.exists()) { // 文件存在时
			System.out.println("文件夹不存在：" + oldDir);
			return false;
		}
		if (!oldDir.isDirectory()) { // 文件存在时
			System.out.println(oldDir + "不是文件夹");
			return false;
		}
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File[] files = oldDir.listFiles();
			File temp = null;
			for (int i = 0; i < files.length; i++) {
				temp = files[i];
				if (temp.isFile()) {
					if (!FileUtil.copyFile(temp, newPath + "/" + temp.getName())) {
						return false;
					}
				} else if (temp.isDirectory()) {// 如果是子文件夹
					if (!FileUtil.copyDir(temp, newPath + "/" + temp.getName())) {
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错。错误原因:" + e.getMessage());
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移动文件到指定目录
	 */
	public static boolean move(String oldPath, String newPath) {
		oldPath = normalizePath(oldPath);
		newPath = normalizePath(newPath);
		return copy(oldPath, newPath) && delete(oldPath);
	}

	/**
	 * 移动文件到指定目录
	 */
	public static boolean move(File oldFile, String newPath) {
		oldFile = normalizeFile(oldFile);
		newPath = normalizePath(newPath);
		return copy(oldFile, newPath) && delete(oldFile);
	}

	/**
	 * 将可序列化对象序列化并写入指定文件
	 */
	public static void serialize(Serializable obj, String fileName) {
		fileName = normalizePath(fileName);
		try {
			FileOutputStream f = new FileOutputStream(fileName);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(obj);
			s.flush();
			s.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将可序列化对象序列化并返回二进制数组
	 */
	public static byte[] serialize(Serializable obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream s = new ObjectOutputStream(b);
			s.writeObject(obj);
			s.flush();
			s.close();
			return b.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 从指定文件中反序列化对象
	 */
	public static Object unserialize(String fileName) {
		fileName = normalizePath(fileName);
		try {
			FileInputStream in = new FileInputStream(fileName);
			ObjectInputStream s = new ObjectInputStream(in);
			Object o = s.readObject();
			s.close();
			return o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 从二进制数组中反序列化对象
	 */
	public static Object unserialize(byte[] bs) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			ObjectInputStream s = new ObjectInputStream(in);
			Object o = s.readObject();
			s.close();
			return o;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static void main(String[] args) {
		File f = new File("C:\\Users\\zhangyuyang1\\Desktop\\单点登录附件.docx");
//		System.out.println(f.list().length);
//		System.out.println(f.getAbsolutePath());
        try {
            InputStream inputStream = new FileInputStream(f);
			System.out.println(FileUtil.getTypeByFile(f));
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

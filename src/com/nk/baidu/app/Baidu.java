package com.nk.baidu.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.nk.baidu.helper.ReadFileHelper;
import com.nk.baidu.util.FileUtil;

public class Baidu {

	private final static String KEY_WORDS = "庆祝厦门供电段";
	private final static String FIND_DIR = "F:\\XMGDD";
	public static void main(String[] args) {
		List<File> listFile = findFiles(FIND_DIR);
		for(File file : listFile){
			String str = Baidu.analyseFile(file.getAbsolutePath(),KEY_WORDS);
			if(StringUtils.isNotBlank(str))
				System.out.println(str);
		}
		System.out.println("共"+listFile.size());
	}
	
	public static List<File> findFiles(String path){
		List<File> fileList = new ArrayList<File>();
		File filelist = new File(path);
		String[] lists = filelist.list(); 
		if(lists!=null && lists.length>0){
			for(String list : lists){
				File tempFile = new File(path +"\\"+ list);
				System.out.println(path+"\\"+list);
				if(tempFile.isFile()){
					fileList.add(tempFile);
				}else{
					List<File> tempList = Baidu.findFiles(tempFile.getAbsolutePath());
					if(tempList!=null && tempList.size()>0){
						fileList.addAll(tempList);
					}
				}
			}
		}
		return fileList;
	}
	
	private static String analyseFile(String path,String keyWord)  {
		
		File file = new File(path);
		String content = null;
		//判断文件是否存在
		if(!file.exists()){
			return content;
		}
		try {		
			if(path.contains(".")){
				String suffix = path.substring(path.lastIndexOf(".")+1);
				if(suffix.contains("doc")){//doc
					content = ReadFileHelper.readWord(path);
				}else if(suffix.contains("ppt")){//ppt
					content = ReadFileHelper.readPowerPoint(path);
				}else if(suffix.contains("pdf")){//pdf
					content = ReadFileHelper.readPdf(path);
				}else if(suffix.contains("txt")){//txt
					content = ReadFileHelper.readTxt(path);
				}else if(suffix.contains("xls")){//xls
					content = ReadFileHelper.readExcel(path);
				}	
			}
		} catch (Exception e) {
			FileUtil.writeToFile("D:/baidu.log", e.getMessage());
			e.printStackTrace();
		}
		if(content!=null&&content.contains(keyWord)){
			System.out.println("==========找到啦---"+keyWord);
			return path;
		}else{
			return null;
		}
	}

}

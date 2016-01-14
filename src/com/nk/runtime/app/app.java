package com.nk.runtime.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class app {

	public static void main(String[] args) {
		try {
			Process process = Runtime.getRuntime().exec("ping 192.168.10.238");
			BufferedInputStream inputStream = (BufferedInputStream) process.getInputStream();
			BufferedReader inBr = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null){
				System.out.println(lineStr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

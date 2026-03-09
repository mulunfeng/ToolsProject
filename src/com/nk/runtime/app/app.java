package com.nk.runtime.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class app {

	public static void main(String[] args) {
		String[] cmd = new String[3];

		Process process;
//			process = Runtime.getRuntime().exec("ping 192.168.10.238");
//			print(process);
//			process = Runtime.getRuntime().exec("java");
//			print(process);


		while (true) {
			Scanner input=new Scanner(System.in);
			String str=input.nextLine();
			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			cmd[2] = str;
            try {
                process = Runtime.getRuntime().exec(cmd);
                print(process);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
			System.out.println("---------------------------");
			try {
                process = Runtime.getRuntime().exec(str);
                print(process);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

	}

	public static void print(Process process) {
		try {
			BufferedInputStream inputStream = (BufferedInputStream) process.getInputStream();
			BufferedReader inBr = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null){
				System.out.println(lineStr);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}

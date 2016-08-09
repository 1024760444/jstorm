package com.alibaba.jstorm.ui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * String write  to file or read string from file.
 * @author yanghaitao
 *
 */
public class StringFileUtil {
	/**
	 * write data to file.
	 * @param data
	 * @param file
	 */
	public static void writeTo(String data, File file) {
		FileWriter fw = null;
		BufferedWriter out = null;
		try {
			fw = new FileWriter(file);
			out = new BufferedWriter(fw);
			out.write(data);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Read Data from File.
	 * @param file
	 */
	public static String readFrom(File file) {
		FileReader fr = null;
		BufferedReader br = null;
		
		StringBuffer stringBuffer = new StringBuffer();
		try {
			String line = null;
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stringBuffer.toString();
	}
}

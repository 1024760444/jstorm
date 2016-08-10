package com.alibaba.jstorm.ui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.jstorm.ui.model.UIProperties;

/**
 * Script run
 * 
 * @author yanghaitao
 *
 */
public class ScriptUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ScriptUtil.class);
	public static String DEFAULT_PATH = "/home/jstorm/apps";
	public static String DEFAULT_PWD = "";
	
	public static String PROPERT_PATH = "file.path";
	public static String PROPERT_PWD = "behavior.pwd";
	
	public static String UIPRPPERTIES_PATH = System.getProperty("user.home") + "/.jstorm/storm-ui.properties";
	
	/**
	 * deactivate or kill the topoName.
	 * @param behavior kill or deactivate 
	 * @param topoName topoName
	 */
	public static void run(String behavior, String topoName) {
		Process process = null;
		InputStreamReader ir = null;
		LineNumberReader input = null;
		try {
			process = Runtime.getRuntime()
					.exec(new String[] { "/bin/sh", "-c", "jstorm " + behavior + " " + topoName }, null, null);

			ir = new InputStreamReader(process.getInputStream());
			input = new LineNumberReader(ir);
			String line;
			process.waitFor();
			StringBuffer sbuffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				sbuffer.append(line).append("\n");
			}
			LOG.info(UIPRPPERTIES_PATH + ", Run : jstorm " + behavior + " " + topoName + " , " + sbuffer.toString());
		} catch (IOException e) {
			LOG.error("Run : jstorm " + behavior + " " + topoName + " , " + e.getMessage(), e);
		} catch (InterruptedException e) {
			LOG.error("Run : jstorm " + behavior + " " + topoName + " , " + e.getMessage(), e);
		} finally {
			if(process != null) {
				process.destroy();
			} 
			if(ir != null) {
				try {
					ir.close();
				} catch (IOException e) {
					LOG.error("Run : jstorm " + behavior + " " + topoName + ", when close ir , " + e.getMessage(), e);
				}
			}
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOG.error("Run : jstorm " + behavior + " " + topoName + ", when close input , " + e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * submit topology task
	 * @param jarPath
	 * @param args
	 */
	public static void submit(String jarPath, String args) {
		Process process = null;
		InputStreamReader ir = null;
		LineNumberReader input = null;
		try {
			process = Runtime.getRuntime()
					.exec(new String[] { "/bin/sh", "-c", "jstorm jar " + jarPath + " " + args }, null, null);

			ir = new InputStreamReader(process.getInputStream());
			input = new LineNumberReader(ir);
			String line;
			process.waitFor();
			StringBuffer sbuffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				sbuffer.append(line).append("\n");
			}
			LOG.info("Run : jstorm jar " + jarPath + " " + args + " , " + sbuffer.toString());
		} catch (IOException e) {
			LOG.error("Run : jstorm jar " + jarPath + " " + args + " , " + e.getMessage(), e);
		} catch (InterruptedException e) {
			LOG.error("Run : jstorm jar " + jarPath + " " + args + " , " + e.getMessage(), e);
		} finally {
			if(process != null) {
				process.destroy();
			} 
			if(ir != null) {
				try {
					ir.close();
				} catch (IOException e) {
					LOG.error("Run : jstorm jar " + jarPath + " " + args + ", when close ir , " + e.getMessage(), e);
				}
			}
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOG.error("Run : jstorm jar " + jarPath + " " + args + ", when close input , " + e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Get the ui properties
	 * @param path
	 * @return
	 */
	public static UIProperties properties() {
		Properties dbProps = new Properties();
		InputStream in = null;
		UIProperties propert = new UIProperties(DEFAULT_PATH, DEFAULT_PWD);
		try {
			in = new FileInputStream(new File(UIPRPPERTIES_PATH));
			// in = ClassLoader.getSystemResourceAsStream(UIPRPPERTIES_PATH);
			dbProps.load(in);
			propert.setPath(dbProps.getProperty(PROPERT_PATH));
			propert.setPwd(dbProps.getProperty(PROPERT_PWD));
		} catch (Exception e) {
			LOG.error("Read properties file : " + UIPRPPERTIES_PATH + " failed, ", e);
		}
		return propert;
	}
	
	/**
	 * split the string with "/", and get the Last string.
	 * @param jarName
	 * @return
	 */
	public static String splitName(String jarName) {
		String[] nameList = jarName.split("\\/");
		
		String pkgName = null;
		if(nameList != null && nameList.length > 0) {
			pkgName = nameList[nameList.length - 1];
		}
		return pkgName;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String jarName = "/home/jstorm/apps/APM/apm1.3/bin/original-apmhub011.3.jar";
		System.out.println(splitName(jarName));
	}
}

package com.alibaba.jstorm.ui.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Script run
 * 
 * @author yanghaitao
 *
 */
public class ScriptUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ScriptUtil.class);
	
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
			LOG.info("Run : jstorm " + behavior + " " + topoName + " , " + sbuffer.toString());
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
}

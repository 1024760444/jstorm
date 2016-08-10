package com.alibaba.jstorm.ui.model;

/**
 * show jar info
 * @author yanghaitao
 *
 */
public class UIJarFile {
	private String jarName;
	private String fullPath;
	private String args;
	private String keys;
	public String getJarName() {
		return jarName;
	}
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
}

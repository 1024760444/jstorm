package com.alibaba.jstorm.ui.model;

/**
 * UI info
 * @author yanghaitao
 *
 */
public class UIProperties {
	/**
	 * Submit file path
	 */
	private String path;
	
	/**
	 * pwd of the ui behavior.
	 */
	private String pwd;
	
	public UIProperties(String path, String pwd) {
		this.path = path;
		this.pwd = pwd;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}

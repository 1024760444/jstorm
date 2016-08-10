package com.alibaba.jstorm.ui.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.jstorm.ui.model.ClusterEntity;
import com.alibaba.jstorm.ui.model.UIJarFile;
import com.alibaba.jstorm.ui.model.UIProperties;
import com.alibaba.jstorm.ui.utils.NimbusClientManager;
import com.alibaba.jstorm.ui.utils.ScriptUtil;
import com.alibaba.jstorm.ui.utils.StringFileUtil;
import com.alibaba.jstorm.ui.utils.UIUtils;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.utils.NimbusClient;

/**
 * Tasks 
 * @author yanghaitao
 *
 */
@Controller
public class JarListController {
	private static final Logger LOG = LoggerFactory.getLogger(JarListController.class);
	
	/**
	 * click it, go to task(jar) list. 
	 * @return page
	 */
	@RequestMapping(value = "/toTasks", method = RequestMethod.GET)
	public String toTaskList(@RequestParam(value = "name", required = true) String name, ModelMap model) {
		
		// Get the jar info array.
		List<UIJarFile> jarFileArray = getJarFiles();
		
		// cluster info
		long start = System.currentTimeMillis();
		NimbusClient client = null;
		try {
			client = NimbusClientManager.getNimbusClient(name);
	        ClusterSummary clusterSummary = client.getClient().getClusterInfo();
			//update cluster cache
	        ClusterEntity ce = UIUtils.getClusterEntity(clusterSummary, name);
	        model.addAttribute("cluster", ce);
	        UIUtils.clustersCache.put(name, ce);
		} catch(Exception e) {
			NimbusClientManager.removeClient(name);
            LOG.error(e.getMessage(), e);
            UIUtils.addErrorAttribute(model, e);
		}
		
		// 
		model.addAttribute("jarFileArray", jarFileArray);
		model.addAttribute("clusterName", name);
        UIUtils.addTitleAttribute(model, "Cluster Summary");
        LOG.info("cluster page show cost:{}ms", System.currentTimeMillis() - start);
		return "jarList";
	}
	
	/**
	 * uopload jars to The server, and save it's args and passwd. 
	 * @param name
	 * @param model
	 * @param inputfile
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadJar", method = RequestMethod.POST)
	public ModelAndView uploadFile(@RequestParam(value = "name", required = true) String name, ModelMap model, 
			@RequestParam(value = "inputfile", required = false) MultipartFile inputfile, HttpServletRequest request) {
		UIProperties properties = ScriptUtil.properties();
		
		String jarName = request.getParameter("jarName");
		String mainClass = request.getParameter("mainClass");
		String mainArgs = request.getParameter("mainArgs");
		String keyword = request.getParameter("keyword");
		
		String argsString = mainClass + " " + mainArgs;
		
		// mkdir path 
		File targetFolder = new File(properties.getPath());
		File argsFolder = new File(properties.getPath() + "/args");
		File keysFolder = new File(properties.getPath() + "/keys");
		if(!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		
		// 
		if(!argsFolder.exists()) {
			argsFolder.mkdirs();
		}
		
		//
		if (!keysFolder.exists()) {
			keysFolder.mkdirs();
		}
		
		/**
		 * args to the file.
		 */
		File argsFile = new File(properties.getPath() + "/args/" + jarName + ".txt");
		StringFileUtil.writeTo(argsString, argsFile);
		
		/**
		 * args to the file.
		 */
		File keysFile = new File(properties.getPath() + "/keys/" + jarName + ".txt");
		StringFileUtil.writeTo(keyword, keysFile);
		
		/**
		 * save data 
		 */
		try {
			File jarFile = new File(properties.getPath() + "/" + jarName + ".jar");
			inputfile.transferTo(jarFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/toTasks?name=" + name);
	}
	
	/**
	 * submit Topology, but if the passwd is not Correct, failed.
	 * @param name
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/submitTopology", method = RequestMethod.POST)
	public ModelAndView submitTopology(@RequestParam(value = "name", required = true) String name, 
			ModelMap model, HttpServletRequest request) {
		String submitTopoJar = request.getParameter("submitTopoJar");
		String submitTopoPath = request.getParameter("submitTopoPath");
		String submitTopoArgs = request.getParameter("submitTopoArgs");
		String passWord = request.getParameter("passWord");
		
		// Get the jar info array.
		List<UIJarFile> jarFileArray = getJarFiles();
		for(UIJarFile jarFile : jarFileArray) {
			String jarName = jarFile.getJarName();
			String keys = jarFile.getKeys();
			
			boolean isPass = submitTopoJar.equals(jarName) && (keys == null || "".equals(keys) || keys.equals(passWord));
			LOG.info("isPass = " + isPass + ", and submitTopoJar = " + submitTopoJar
					+ ", and jarName = " + jarName
					+ ", and keys = " + keys
					+ ", and passWord = " + passWord);
			if(isPass) {
				ScriptUtil.submit(submitTopoPath, submitTopoArgs);
				break;
			}
		}
		
		return new ModelAndView("redirect:/cluster?name=" + name);
	}
	
	/**
	 * Get the jar info array.
	 * @return
	 */
	private List<UIJarFile> getJarFiles() {
		
		// read the ui properties
		UIProperties properties = ScriptUtil.properties();
		String jarPath = properties.getPath();
		String argsPath = properties.getPath() + "/args";
		String keysPath = properties.getPath() + "/keys";
		
		// get the jar's keys
		Map<String, String> jarKeysMap = new HashMap<String, String>();
		File keysFolder = new File(keysPath);
		File[] keyslistFiles = keysFolder.listFiles();
		if(keyslistFiles != null) {
			for(File keysFile : keyslistFiles) {
				String keysFileName = keysFile.getName();
				if(keysFile.isFile() && keysFileName.endsWith(".txt")) {
					keysFileName = keysFileName.substring(0, keysFileName.length() - 4);
					String data = StringFileUtil.readFrom(keysFile);
					jarKeysMap.put(keysFileName, data);
				}
			}
		}
		
		// get the jar's args
		Map<String, String> jarArgsMap = new HashMap<String, String>();
		File argsFolder = new File(argsPath);
		File[] argslistFiles = argsFolder.listFiles();
		if(argslistFiles != null) {
			for(File argsFile : argslistFiles) {
				String argsFileName = argsFile.getName();
				if(argsFile.isFile() && argsFileName.endsWith(".txt")) {
					argsFileName = argsFileName.substring(0, argsFileName.length() - 4);
					String data = StringFileUtil.readFrom(argsFile);
					jarArgsMap.put(argsFileName, data);
				}
			}
		}
		
		// get zhe jar list
		List<UIJarFile> jarFileArray = new ArrayList<UIJarFile>();
		File jarFolder = new File(jarPath);
		File[] listFiles = jarFolder.listFiles();
		if(listFiles != null) {
			for(File jarfile : listFiles) {
				String jarFileName = jarfile.getName();
				if(jarfile.isFile() && jarFileName.endsWith(".jar")) {
					jarFileName = jarFileName.substring(0, jarFileName.length() - 4);
					
					UIJarFile fileObject = new UIJarFile();
					fileObject.setJarName(jarfile.getName());
					fileObject.setFullPath(jarfile.getPath());
					String args = jarArgsMap.get(jarFileName);
					String keys = jarKeysMap.get(jarFileName);
					if(args != null) {
						fileObject.setArgs(args);
					}
					if(keys != null) {
						fileObject.setKeys(keys);
					}
					jarFileArray.add(fileObject);
				}
			}
		}
		return jarFileArray;
	}
}

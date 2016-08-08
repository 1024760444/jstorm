package com.alibaba.jstorm.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.jstorm.ui.model.ClusterEntity;
import com.alibaba.jstorm.ui.utils.NimbusClientManager;
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
		long start = System.currentTimeMillis();
		model.addAttribute("name", name);
		model.addAttribute("path", System.getProperty("user.dir"));
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
		
		model.addAttribute("clusterName", name);
        UIUtils.addTitleAttribute(model, "Cluster Summary");
        LOG.info("cluster page show cost:{}ms", System.currentTimeMillis() - start);
		return "jarList";
	}
}

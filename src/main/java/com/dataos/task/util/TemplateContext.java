package com.dataos.task.util;

import java.util.Map;

import org.springframework.beans.factory.support.ManagedMap;

/**
 * Copyright © 砥特信息科技有限公司. All rights reserved.
 *
 * @Title: TmeplateContext.java
 * @Prject: dataos
 * @Package: com.dt.dataos.tools
 * @Description: 用来存储需要替换的模版的内容
 * @author: yangguowen
 * @date: Feb 24, 2019 6:53:58 PM
 * @version: V1.0
 */
public class TemplateContext {
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	private String templateName;//使用的模版
	private String taskId;//任务id
	private String taskRootPath;//文件存放根路径 以“/”结尾
	private String taskTargetPath;//文件存放路径
	private String taskName;//任务名称
	private Map<String, Object> map;//用来存放模版中需要用到的键值对
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskRootPath() {
		return taskRootPath;
	}
	public void setTaskRootPath(String taskRootPath) {
		this.taskRootPath = taskRootPath;
	}
	public String getTaskTargetPath() {
		return taskTargetPath;
	}
	public void setTaskTargetPath(String taskTargetPath) {
		this.taskTargetPath = taskTargetPath;
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Map<String, Object> getMap() {
		return map;
	}
	public void setKeyValue(String key, String value) {
		this.map.put(key, value);
	}
	
}

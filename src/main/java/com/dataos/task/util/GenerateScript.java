package com.dataos.task.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 脚本生成工具
 * 
 * @author 杨国文
 *
 */
@Service
public class GenerateScript {

    @Autowired
    Configuration configuration;
    @Autowired
    TemplateProperties templateProperties;

	public void doGenerateScript(TemplateContext tc) throws Exception {
		String taskPath = tc.getTaskRootPath() + tc.getTaskTargetPath() + tc.getTaskName();
		this.generateFileByTemplate(tc.getTemplateName(), taskPath, tc.getMap());
	}

	/**
	 * 根据脚本生成
	 * 
	 * @param templateName 模版名称
	 * @param file         脚本生成后写入地址
	 * @param dataMap      需要替换的内容：key 需要替换关键字；value 替换的内容
	 * @throws Exception
	 */
	public void generateFileByTemplate(final String templateName, String file, Map<String, Object> dataMap)
			throws Exception {
		Template template = configuration.getTemplate(templateName);
		FileOutputStream fos = new FileOutputStream(new File(file));
		Writer out = new BufferedWriter(
				new OutputStreamWriter(fos, templateProperties.getCharset()), 10240);
		template.process(dataMap, out);
	}
}

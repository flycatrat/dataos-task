package com.dataos.task.service.impl;

import com.dataos.task.config.GlobalConfig;
import com.dataos.task.constants.Constant;
import com.dataos.task.service.IAzkabanService;

import com.dataos.task.util.SSLUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by libing on ${date}. </br>
 **/
public class AzkabanServiceImpl implements IAzkabanService {

    private static final Logger logger = LoggerFactory.getLogger(AzkabanServiceImpl.class);

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=utf-8";

    private static final String X_REQUESTED_WITH = "XMLHttpRequest";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GlobalConfig globalConfig;

    @Override
    public String login() throws Exception {
        SSLUtil.turnOffSslChecking();
        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", CONTENT_TYPE);
        hs.add("X-Requested-With", X_REQUESTED_WITH);
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        linkedMultiValueMap.add("action", "login");
        linkedMultiValueMap.add("username", globalConfig.getAzkUsername());
        linkedMultiValueMap.add("password", globalConfig.getAzkPassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
        String result = restTemplate.postForObject(globalConfig.getAzkUrl(), httpEntity, String.class);

        logger.info("--------Azkaban返回登录信息：" + result);

        return new Gson().fromJson(result, JsonObject.class).get("session.id").getAsString();
    }

    @Override
    public void createProject(String projectName, String description) throws Exception {
        SSLUtil.turnOffSslChecking();
        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", CONTENT_TYPE);
        hs.add("X-Requested-With", X_REQUESTED_WITH);
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        linkedMultiValueMap.add("session.id", login());
        linkedMultiValueMap.add("action", "create");
        linkedMultiValueMap.add("name", projectName);
        linkedMultiValueMap.add("description", description);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
        String result = restTemplate.postForObject(globalConfig.getAzkUrl() + "/manager", httpEntity, String.class);

        logger.info("--------Azkaban返回创建Project信息：" + result);

        // 创建成功和已存在，都表示创建成功
        if (!Constant.AZK_SUCCESS.equals(new Gson().fromJson(result, JsonObject.class).get("status").getAsString())) {
            if (!"Project already exists.".equals(new Gson().fromJson(result, JsonObject.class).get("message").getAsString())) {
                throw new Exception("创建Azkaban Project失败");
            }
        }
    }

    @Override
    public void deleteProject(String projectName) throws Exception {
        SSLUtil.turnOffSslChecking();

        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", CONTENT_TYPE);
        hs.add("X-Requested-With", X_REQUESTED_WITH);
        hs.add("Accept", "text/plain;charset=utf-8");

        Map<String, String> map = new HashMap<>();

        map.put("id", login());
        map.put("project", projectName);

        ResponseEntity<String> exchange = restTemplate.exchange(globalConfig.getAzkUrl() + "/manager?session.id={id}&delete=true&project={project}", HttpMethod.GET, new HttpEntity<String>(hs),
                String.class, map);

        logger.info("--------Azkaban返回删除Azkaban Project信息：" + exchange);

        if (HttpStatus.SC_OK != exchange.getStatusCodeValue()) {
            throw new Exception("删除Azkaban Project失败");
        }
    }

    @Override
    public String uploadZip(String projectName, File file) throws Exception {
        SSLUtil.turnOffSslChecking();
        FileSystemResource resource = new FileSystemResource(file);
        LinkedMultiValueMap<String, Object> linkedMultiValueMap = new LinkedMultiValueMap<String, Object>();
        linkedMultiValueMap.add("session.id", login());
        linkedMultiValueMap.add("ajax", "upload");
        linkedMultiValueMap.add("project", projectName);
        linkedMultiValueMap.add("file", resource);
        String result = restTemplate.postForObject(globalConfig.getAzkUrl() + "/manager", linkedMultiValueMap, String.class);

        logger.info("--------Azkaban返回上传文件信息：" + result);

        if (StringUtils.isEmpty(new Gson().fromJson(result, JsonObject.class).get("projectId").getAsString())) {
            throw new Exception("上传文件至Azkaban失败");
        }

        return new Gson().fromJson(result, JsonObject.class).get("projectId").getAsString();
    }

    @Override
    public String scheduleEXEaFlow(String projectId, String projectName, String flow, String flowName, String recurring, String period, Date date) throws Exception {
        SSLUtil.turnOffSslChecking();
        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", CONTENT_TYPE);
        hs.add("X-Requested-With", X_REQUESTED_WITH);
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        linkedMultiValueMap.add("session.id", login());
        linkedMultiValueMap.add("ajax", "scheduleFlow");
        linkedMultiValueMap.add("projectName", projectName);
        linkedMultiValueMap.add("projectId", projectId);
        linkedMultiValueMap.add("flow", flow);
        linkedMultiValueMap.add("flowName", flowName);
        linkedMultiValueMap.add("is_recurring", recurring);
        linkedMultiValueMap.add("period", period);
        scheduleTimeInit(linkedMultiValueMap, date);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
        String result = restTemplate.postForObject(globalConfig.getAzkUrl() + "/schedule", httpEntity, String.class);

        logger.info("--------Azkaban返回根据时间创建定时任务信息：" + result);

        if (!Constant.AZK_SUCCESS.equals(new Gson().fromJson(result, JsonObject.class).get("status").getAsString()) || new Gson().fromJson(result, JsonObject.class).get("scheduleId").getAsInt() < 0) {
            throw new Exception("根据时间创建定时任务失败");
        }

        return new Gson().fromJson(result, JsonObject.class).get("scheduleId").getAsString();
    }

    private void scheduleTimeInit(LinkedMultiValueMap<String, String> linkedMultiValueMap, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer day = calendar.get(Calendar.DATE);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);

        linkedMultiValueMap.add("scheduleTime", hour + "," + minute + (hour > 11 ? ",pm,PDT" : ",am,EDT"));
        linkedMultiValueMap.add("scheduleDate", month + "/" + day + "/" + year);
    }

    @Override
    public String scheduleByCronEXEaFlow(String projectName, String cron, String flow, String flowName) throws Exception {
        SSLUtil.turnOffSslChecking();
        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", CONTENT_TYPE);
        hs.add("X-Requested-With", X_REQUESTED_WITH);
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        linkedMultiValueMap.add("session.id", login());
        linkedMultiValueMap.add("ajax", "scheduleCronFlow");
        linkedMultiValueMap.add("projectName", projectName);
        linkedMultiValueMap.add("cronExpression", cron);
        linkedMultiValueMap.add("flow", flow);
        linkedMultiValueMap.add("flowName", flowName);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
        String result = restTemplate.postForObject(globalConfig.getAzkUrl() + "/schedule", httpEntity, String.class);

        logger.info("--------Azkaban返回根据cron表达式创建定时任务信息：" + result);

        if (!Constant.AZK_SUCCESS.equals(new Gson().fromJson(result, JsonObject.class).get("status").getAsString())) {
            throw new Exception("根据cron表达式创建定时任务失败");
        }

        return new Gson().fromJson(result, JsonObject.class).get("scheduleId").getAsString();
    }

    @Override
    public void unscheduleFlow(String scheduleId) throws Exception {
        SSLUtil.turnOffSslChecking();
        HttpHeaders hs = new HttpHeaders();
        hs.add("Content-Type", CONTENT_TYPE);
        hs.add("X-Requested-With", X_REQUESTED_WITH);
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
        linkedMultiValueMap.add("session.id", login());
        linkedMultiValueMap.add("action", "removeSched");
        linkedMultiValueMap.add("scheduleId", scheduleId);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(linkedMultiValueMap, hs);
        String result = restTemplate.postForObject(globalConfig.getAzkUrl() + "/schedule", httpEntity, String.class);

        logger.info("--------Azkaban返回取消流调度信息：" + result);

        if (!Constant.AZK_SUCCESS.equals(new Gson().fromJson(result, JsonObject.class).get("status").getAsString())) {
            throw new Exception("根据cron表达式创建定时任务失败");
        }
    }

    @Override
    public void downLoadZip(String projectName, String zipPath) {
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;

        try {
            URL url = new URL(globalConfig.getAzkUrl() + "/manager?session.id=" + login() + "&download=true&project=" + projectName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            InputStream inputStream = conn.getInputStream();
            File file = new File(zipPath);
            output = new FileOutputStream(file);
            bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            logger.info("--------下载Azkaban压缩文件异常：" + e.getMessage(), e);
        } finally {
            if (bufferedOutput != null) {
                try {
                    bufferedOutput.flush();
                    bufferedOutput.close();
                } catch (IOException e) {
                    logger.info("关闭流异常：" + e.getMessage(), e);
                }
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.info("关闭流异常：" + e.getMessage(), e);
                }
            }
        }

    }
}

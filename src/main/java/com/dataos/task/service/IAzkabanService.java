package com.dataos.task.service;

import java.io.File;
import java.util.Date;

/**
 * Create by libing on ${date}. </br>
 **/
public interface IAzkabanService {
        /**
         * Azkaban登录接口，返回sessionId
         * @author wuzy
         * @date 2017年12月21日
         * @return
         * @throws Exception
         */
        public String login() throws Exception;

        /**
         * Azkaban创建project
         * @author wuzy
         * @date 2017年12月21日
         * @param projectName project名称
         * @param description project描述
         * @throws Exception
         */
        public void createProject(String projectName, String description) throws Exception;

        /**
         * Azkaban删除project
         * @author wuzy
         * @date 2017年12月21日
         * @param projectName project名称
         * @throws Exception
         */
        public void deleteProject(String projectName) throws Exception;

        /**
         * Azkaban上传zip文件
         * @author wuzy
         * @date 2017年12月21日
         * @param projectName
         * @param file
         * @return projectId
         * @throws Exception
         */
        public String uploadZip(String projectName, File file) throws Exception;

        /**
         * 根据时间 创建调度任务
         * @author wuzy
         * @date 2017年12月21日
         * @param projectId
         * @param projectName
         * @param flow
         * @param flowName
         * @param recurring 是否循环，on循环
         * @param period 循环频率： M Months，w Weeks，d Days，h Hours，m Minutes，s Seconds；如60s，支持分钟的倍数
         * @param date 开始时间
         * @return 返回scheduleId
         * @throws Exception
         */
        public String scheduleEXEaFlow(String projectId, String projectName, String flow, String flowName, String recurring, String period, Date date) throws Exception;

        /**
         * 根据cron表达式 创建调度任务
         * @author wuzy
         * @date 2017年12月21日
         * @param projectName
         * @param cron
         * @param flow
         * @param flowName
         * @return 返回scheduleId
         * @throws Exception
         */
        public String scheduleByCronEXEaFlow(String projectName, String cron, String flow, String flowName) throws Exception;

        /**
         * 根据scheduleId取消一个流的调度
         * @author wuzy
         * @date 2017年12月21日
         * @param scheduleId
         * @throws Exception
         */
        public void unscheduleFlow(String scheduleId) throws Exception;

        /**
         * 下载Azkaban压缩文件
         * @author wuzy
         * @date 2017年12月22日
         * @param projectName
         * @param zipPath
         * @throws Exception
         */
        public void downLoadZip(String projectName, String zipPath) throws Exception;
}

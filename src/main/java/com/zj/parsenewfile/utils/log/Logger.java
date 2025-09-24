package com.zj.parsenewfile.utils.log;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.ProjectManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * @author : jie.zhou
 * @date : 2025/9/11
 */
public class Logger {

    private final com.intellij.openapi.diagnostic.Logger log;

    private Logger(com.intellij.openapi.diagnostic.Logger log) {
        this.log = log;
    }

    public static Logger getInstance(Class<?> clazz) {
        return new Logger(com.intellij.openapi.diagnostic.Logger.getInstance(clazz));
    }

    public void log(String msg) {
        info(msg);
    }

    public void info(String message) {
        notice(message);
    }

    public void error(Throwable throwable) {
        ProjectManager.getInstance().getDefaultProject();
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            error(sw.toString());
        } catch (IOException e) {
            error("printStackTrace error " + e.getMessage());
        }
    }

    public void error(String msg) {
        errorNotice(msg);
    }

    private void notice(String message) {
        // 仅调试时开启，信息显示在第一个project里
        // 发布分支需关闭
        Optional.of(ProjectManager.getInstance().getOpenProjects())
                .filter(openProjects -> openProjects.length > 0)
                .map(openProjects -> openProjects[0])
                .ifPresent(project -> NotificationGroup.create("com.zj.mybatis-utils-notice", NotificationDisplayType.BALLOON,
                                true, "com.zj.mybatis-utils", "Mybatis utils", PluginId.getId("com.zj.mybatis-utils"))
                        .createNotification(message, NotificationType.INFORMATION)
                        .notify(project)
                );

        log.info(message);
    }

    private void errorNotice(String message) {
        // 仅调试时开启，信息显示在第一个project里
        // 发布分支需关闭
        Optional.of(ProjectManager.getInstance().getOpenProjects())
                .filter(openProjects -> openProjects.length > 0)
                .map(openProjects -> openProjects[0])
                .ifPresent(project -> NotificationGroup.create("com.zj.mybatis-utils-notice", NotificationDisplayType.BALLOON,
                                true, "com.zj.mybatis-utils", "Mybatis utils", PluginId.getId("com.zj.mybatis-utils"))
                        .createNotification(message, NotificationType.ERROR)
                        .notify(project)
                );

        log.error(message);
    }
}

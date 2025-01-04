package io.github.natank25.epitechutils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.NotificationsManager;
import com.intellij.openapi.project.Project;

public class EpitechNotifications {
	
	public static void sendNotification(Project project, String content, NotificationType notificationType) {
		NotificationsManager.getNotificationsManager().showNotification(new Notification("EpitechUtils", content, notificationType), project);
	}
}

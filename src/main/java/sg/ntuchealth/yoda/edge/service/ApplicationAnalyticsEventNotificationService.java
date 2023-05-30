package sg.ntuchealth.yoda.edge.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Component;
import sg.ntuchealth.yoda.edge.service.model.ApplicationAnalyticsEventDto;

@Component
public class ApplicationAnalyticsEventNotificationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ApplicationAnalyticsEventNotificationService.class);

  private static final String NEW_USER_SIGNUP_SNS_SUBJECT = "NEW_USER_SIGNUP";

  @Autowired private NotificationMessagingTemplate notificationMessagingTemplate;

  @Value("${sns.topic.application-analytics-event}")
  private String applicationAnalyticsEventTopic;

  public void sendNewUserSignUpEventNotification(UUID clientId) {
    LOGGER.debug("Send new user signup event notification");
    notificationMessagingTemplate.sendNotification(
        applicationAnalyticsEventTopic,
        ApplicationAnalyticsEventDto.builder()
            .eventName(ApplicationAnalyticsEventDto.EventName.NEW_SIGN_UP)
            .clientId(clientId)
            .build(),
        NEW_USER_SIGNUP_SNS_SUBJECT);
  }
}

package sg.ntuchealth.yoda.edge.service.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAnalyticsEventDto {

  @AllArgsConstructor
  public enum EventName {
    B3_NEW_USER_SIGN_UP("b3_new_user_sign_up"),
    B3_NEW_APPOINTMENT("b3_new_appointment"),
    B3_RECURRING_APPOINTMENT("b3_recurring_appointment"),
    B3_APPOINTMENT_ATTENDED("b3_appointment_attended"),
    B3_APPOINTMENT_RESCHEDULE("b3_appointment_reschedule"),
    B3_APPOINTMENT_NO_SHOW("b3_appointment_no_show"),

    MYNH_NEW_USER_SIGN_UP("mynh_new_user_sign_up"),
    MYNH_NEW_APPOINTMENT("mynh_new_appointment"),
    MYNH_RECURRING_APPOINTMENT("mynh_recurring_appointment"),
    MYNH_APPOINTMENT_ATTENDED("mynh_appointment_attended"),
    MYNH_APPOINTMENT_RESCHEDULE("mynh_appointment_reschedule"),
    MYNH_APPOINTMENT_NO_SHOW("mynh_appointment_no_show");

    private String value;
  }

  private UUID clientId;
  private UUID appointmentId;
  private EventName eventName;
}

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
    NEW_SIGN_UP("new_sign_up"),
    NEW_APPOINTMENT("new_appointment"),
    RECURRING_APPOINTMENT("recurring_appointment"),
    APPOINTMENT_ATTENDED("appointment_attended"),
    APPOINTMENT_RESCHEDULE("appointment_reschedule"),
    APPOINTMENT_NO_SHOW("appointment_no_show");

    private String value;
  }

  private UUID clientId;
  private UUID appointmentId;
  private EventName eventName;
}

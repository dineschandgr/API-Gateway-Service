package sg.ntuchealth.yoda.edge.web;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.ntuchealth.yoda.edge.service.B3TokenService;
import sg.ntuchealth.yoda.edge.service.ClientService;
import sg.ntuchealth.yoda.edge.service.model.ClientLoginResponse;

/**
 * This service is used only for internal individual record management purposes.
 *
 * @author Upuna
 * @since 2023-06-16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("support/management/profile/")
public class ProfileManagementInternalController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProfileManagementInternalController.class);

  @Autowired private ClientService clientService;

  @Autowired private B3TokenService b3TokenService;

  /**
   * @param request list of {@link ClientLoginResponse}
   * @return {@link String} of Operation Status
   */
  @PutMapping("regenerate-b3-tokens")
  public ResponseEntity<String> regenerateTokens(
      @Valid @RequestBody List<ClientLoginResponse> request) {

    LOGGER.info("regenerateB3AccessTokens: {}", request);
    regenerateB3AccessTokens(request);
    return ResponseEntity.ok().build();
  }

  private void regenerateB3AccessTokens(List<ClientLoginResponse> clientLoginResponses) {
    clientLoginResponses.forEach(
        clientLoginResponse -> b3TokenService.regenerateToken(clientLoginResponse));
  }
}

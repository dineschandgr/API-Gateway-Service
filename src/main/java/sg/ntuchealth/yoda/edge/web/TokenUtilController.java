package sg.ntuchealth.yoda.edge.web;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.ntuchealth.yoda.edge.service.TokenUtilService;

@RestController
@RequestMapping("auth")
public class TokenUtilController {

  @Autowired TokenUtilService tokenUtilService;

  @GetMapping("/redis/keys")
  public ResponseEntity<Set<String>> getRedisKeys(
      @RequestHeader(value = "Authorization") String token) throws Exception {
    return ResponseEntity.ok(tokenUtilService.getRedisKeys(token));
  }

  @DeleteMapping("/redis/keys")
  public ResponseEntity<String> deleteRedisKeys(
      @RequestHeader(value = "Authorization") String token) throws Exception {
    tokenUtilService.deleteRedisKeys(token);
    return ResponseEntity.ok("tokens deleted");
  }
}

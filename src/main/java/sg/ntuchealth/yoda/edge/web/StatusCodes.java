package sg.ntuchealth.yoda.edge.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCodes {
  EXISTING_USER(1000, "User token validated"),
  NEW_USER(1001, "User association created"),
  ASSOCIATION_NOT_FOUND_IN_TOKEN(
      1002, "Association Id not found in token. Association exists in DB"),
  TOKEN_EXPIRED(1003, "The token has expired"),
  AUTHORIZATION_ERROR(1004, "Error in token authorization"),
  CLIENT_NOT_FOUND(1005, "User account does not exist"),
  GENERIC_ERROR(1006, "Oops! There was an error."),
  ASSOCIATION_NOT_SAVED_IN_LINK_ID(1007, "Association not saved in LinkID"),
  ASSOCIATION_NOT_FOUND_IN_TOKEN_AND_DB(
      1008, "Association Id not found in token. Association does not exist in DB");

  private final Integer code;
  private final String message;
}

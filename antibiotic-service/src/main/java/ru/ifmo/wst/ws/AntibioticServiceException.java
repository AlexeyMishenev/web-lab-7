package ru.ifmo.wst.ws;

import javax.xml.ws.WebFault;
import lombok.Getter;

@WebFault(faultBean = "ru.ifmo.wst.lab1.ws.AntibioticServiceFault")
public class AntibioticServiceException extends Exception {
  @Getter
  private final AntibioticServiceFault faultInfo;

  public AntibioticServiceException(String message) {
    super(message);
    this.faultInfo = new AntibioticServiceFault(message);
  }

  public AntibioticServiceException(String message, AntibioticServiceFault faultInfo) {
    super(message);
    this.faultInfo = faultInfo;
  }

  public AntibioticServiceException(String message, Throwable cause) {
    super(message, cause);
    this.faultInfo = new AntibioticServiceFault(message);
  }

  public AntibioticServiceException(
      String message, Throwable cause, AntibioticServiceFault faultInfo) {
    super(message, cause);
    this.faultInfo = faultInfo;
  }
}


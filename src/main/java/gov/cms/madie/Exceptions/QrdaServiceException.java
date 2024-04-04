package gov.cms.madie.Exceptions;

public class QrdaServiceException extends RuntimeException {
  public QrdaServiceException(String message, Exception cause) {
    super(message, cause);
  }

  public QrdaServiceException(String message) {
    super(message);
  }
}

package gov.cms.madie.Exceptions;

public class CqmConversionException extends RuntimeException {
  public CqmConversionException(String message, Exception cause) {
    super(message, cause);
  }

  public CqmConversionException(String message) {
    super(message);
  }
}

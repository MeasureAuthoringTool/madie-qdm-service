package gov.cms.madie.Exceptions;

public class PackagingException extends RuntimeException {
  public PackagingException(String message, Exception cause) {
    super(message, cause);
  }
}

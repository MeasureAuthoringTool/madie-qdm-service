package gov.cms.madie.Exceptions;

public class TranslationServiceException extends RuntimeException {

  public TranslationServiceException(String message, Exception cause) {
    super(message, cause);
  }
}

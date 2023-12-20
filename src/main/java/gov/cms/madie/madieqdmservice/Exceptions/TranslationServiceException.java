package gov.cms.madie.madieqdmservice.Exceptions;

public class TranslationServiceException extends RuntimeException {

  public TranslationServiceException(String message, Exception cause) {
    super(message, cause);
  }
}

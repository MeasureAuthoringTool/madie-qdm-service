package gov.cms.madie.Exceptions;

public class HQMFServiceException extends RuntimeException {
  private static final String MESSAGE =
      "An error occurred that caused the HQMF generation to fail.";

  public HQMFServiceException() {
    super(String.format(MESSAGE));
  }
}
package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.HQMFServiceException;
import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.Exceptions.UnsupportedModelException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomErrorHandlerAdvice {
  private final ErrorAttributes errorAttributes;

  @ExceptionHandler({
    TranslationServiceException.class,
    UnsupportedModelException.class,
    PackagingException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  Map<String, Object> handleCustomException(WebRequest request) {
    return getErrorAttributes(request, HttpStatus.BAD_REQUEST);
  }

  // Handle HQMFServiceException separately
  @ExceptionHandler(HQMFServiceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  Map<String, Object> handleHQMFServiceException(WebRequest request) {
    return getErrorAttributes(request, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private Map<String, Object> getErrorAttributes(WebRequest request, HttpStatus httpStatus) {
    ErrorAttributeOptions errorOptions =
        ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE);
    Map<String, Object> errorAttributes =
        this.errorAttributes.getErrorAttributes(request, errorOptions);
    errorAttributes.put("status", httpStatus.value());
    errorAttributes.put("error", httpStatus.getReasonPhrase());
    return errorAttributes;
  }
}

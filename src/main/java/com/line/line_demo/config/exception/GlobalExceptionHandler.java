package com.line.line_demo.config.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResultData<Object> handleServiceException(ServiceException e, HttpServletResponse response) {
       // response.setStatus(e.getStatusCode());
        return ResultData.resultData(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseBody
    public ResultData<Object> handleServiceException(DateTimeParseException e, HttpServletResponse response) {
        // response.setStatus(e.getStatusCode());
        return ResultData.resultData(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultData<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletResponse response) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResultData.resultData(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid Argument data", errors);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResultData<List<String>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) {
        List<String> errors = Arrays.asList(ex.getMessage().split("\n"));
        return ResultData.resultData(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Illegal Argument Exception", errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResultData<Object> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletResponse response) {
        String paramName = ex.getName();
        String paramValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String errorMessage = "Invalid value for parameter '" + paramName + "': " + paramValue;

        return ResultData.resultData(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData<Object> handleServerErrorException(Exception e, HttpServletResponse response) {
        e.printStackTrace();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        if(e.getCause() instanceof ServiceException){
              return ResultData.resultData(
                      ((ServiceException) e.getCause()).getStatusCode(),
                      ((ServiceException) e.getCause()).getMessage()
              );
        }
        return ResultData.resultData(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), ExceptionUtils.getStackTrace(e));
    }

}

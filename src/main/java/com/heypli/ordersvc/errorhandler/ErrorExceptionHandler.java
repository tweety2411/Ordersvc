package com.heypli.ordersvc.errorhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heypli.ordersvc.common.constant.RespCode;
import com.heypli.ordersvc.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handler(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException exception) {

        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();

        Map<String, Object> errors = new HashMap<>();

        for(ObjectError err : allErrors) {
            if(FieldError.class.isAssignableFrom(err.getClass())) {
                log.error("FIELD ERROR: " + err.getDefaultMessage());
                errors.put(((FieldError) err).getField(), err.getDefaultMessage());
            } else {
                errors.put(err.getObjectName(), err.getDefaultMessage());
            }
        }

        String errMsg = "";

        try {
            errMsg = objectMapper.writeValueAsString(errors);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.status(response.getStatus()).body(new CommonResponse(RespCode.ERROR, errMsg));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handler(HttpServletRequest request, HttpServletResponse response, Exception e){
        return ResponseEntity.status(response.getStatus()).body(new CommonResponse(RespCode.ERROR, e.getMessage()));
    }
}

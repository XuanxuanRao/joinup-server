package cn.org.joinup.common.handler;

import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.exception.RateLimitException;
import cn.org.joinup.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 * @author chenxuanrao06@gmail.com
 */
@RestControllerAdvice
public class BindExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Map<String, String>>> handle(BindException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        Map<String, String> errorMap = new LinkedHashMap<>(fieldErrors.size());
        fieldErrors.forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Result.error("参数错误", errorMap));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Result<String>> handle(BadRequestException e) {
        return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Result<String>> handle(RateLimitException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Result.error(e.getMessage()));
    }

}

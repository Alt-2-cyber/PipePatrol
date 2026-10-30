package com.pipepatrol;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.Map;
@RestControllerAdvice(assignableTypes=OperationsController.class)
public class OperationsErrors {
 @ExceptionHandler({IllegalArgumentException.class,SecurityException.class,UnsupportedOperationException.class})
 public ResponseEntity<Map<String,String>> error(RuntimeException e){return ResponseEntity.status(e instanceof SecurityException?HttpStatus.UNAUTHORIZED:e instanceof UnsupportedOperationException?HttpStatus.FORBIDDEN:HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));}
}

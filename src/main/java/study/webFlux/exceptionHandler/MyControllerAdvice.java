package study.webFlux.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyControllerAdvice {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> handle() {
        return new ResponseEntity<>("Something gone horribly wrong!", HttpStatus.BAD_REQUEST);
    }
}
package study.webFlux.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyControllerAdvice {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> handleE(Exception ex) {
        return new ResponseEntity<>("Something gone terribly wrong!\n" +
                "Seems your functions are not valid!", HttpStatus.BAD_REQUEST);
    }
}
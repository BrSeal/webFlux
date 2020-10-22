package study.webFlux.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyControllerAdvice {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> handle(Exception ex) {
        System.out.println("Exception handler start of message =======================================================");
        System.out.println(ex.getLocalizedMessage());
        ex.printStackTrace();
        System.out.println("Exception handler end of message =========================================================");
        return new ResponseEntity<>("Something gone horribly wrong!", HttpStatus.BAD_REQUEST);

    }
}
package study.webFlux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"config","handlers"})
public class CalculatorApplication {
	public static void main(String[] args) {
		SpringApplication.run(CalculatorApplication.class, args);
	}

}

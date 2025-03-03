package com.kitandasmart.backend;

import nu.pattern.OpenCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KitandasmartBeApplication {

	public static void main(String[] args) {
		OpenCV.loadShared();
		SpringApplication.run(KitandasmartBeApplication.class, args);
	}

}

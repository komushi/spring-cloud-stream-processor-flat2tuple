package io.pivotal.spring.cloud.stream.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Flat2TupleProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(Flat2TupleProcessorApplication.class, args);
	}
}

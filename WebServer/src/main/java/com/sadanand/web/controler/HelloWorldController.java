package com.sadanand.web.controler;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
	@RequestMapping("/hello")
	public String root(){
		return "Hello World from Spring Boot";
	}

	@RequestMapping("/hello/{responseDelay}")
	public String helloWorld(@PathVariable(value="responseDelay") long responseDelay){
		try {Thread.sleep(responseDelay);} catch (InterruptedException e) {}
		return "Hello World from Spring Boot. Delay:" + responseDelay;
	}

	@RequestMapping("/goodbye")
	public String goodbyeWorld(){
		return "Goodbye from Spring Boot";
	}
}

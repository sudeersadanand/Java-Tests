package com.sadanand.httpConnection.Pooling;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.sadanand.httpConnection.httpClient.MultiHttpClientConnThread;

@SpringBootApplication
@ComponentScan("com.sadanand.httpConnection")
public class PooledHttpConnectionManagerTestApplication {

	private static final Logger log = LogManager.getLogger(PooledHttpConnectionManagerTestApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(PooledHttpConnectionManagerTestApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			log.info("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

			log.info("Starting thread for testing");
			int i = 1;
			while (true) {
				while(MultiHttpClientConnThread.getThreadCount() < 400) {
					String threadName = "MyThread_" + i++; 
					//System.out.println("Starting Thread Name " + threadName);
					MultiHttpClientConnThread bean = ctx.getBean(MultiHttpClientConnThread.class);
					bean.setName(threadName);
					bean.start();
				}
				Thread.sleep(500);
				//System.out.println("Active Threads:" + Thread.activeCount());
			}
		};
	}
}

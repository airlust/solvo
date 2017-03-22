package com.curvedpin.services;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class SolvoServicesApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SolvoServicesApplication().configure(new SpringApplicationBuilder(SolvoServicesApplication.class)).run(args);
	}
}

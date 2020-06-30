package com.ecommerce.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
@EnableJpaRepositories(basePackages = "com.ecommerce.basic.repositories")
public class BasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicApplication.class, args);
	}

	@Bean
	// hit {base url}/swagger-ui.html to open API documentation.
	public Docket swaggerConfiguration(){
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.ecommerce.basic"))
				.build()
				.apiInfo(apiDetails());
	}

	private ApiInfo apiDetails(){
		return new ApiInfo(
				"Basic-e-commerce-rest-APIs",
				"Apis from Authentication, browsing categories/products, cart management," +
							" order management to checkout for basic ecommerce web application",
				"1.0",
				"https://raw.githubusercontent.com/shrikant7/Basic-e-commerce-rest-APIs/master/LICENSE",
				new Contact("Shrikant Sharma","https://www.linkedin.com/in/shrikant007/", "shrikant.lnmiit@gmail.com"),
				"MIT License",
				"https://raw.githubusercontent.com/shrikant7/Basic-e-commerce-rest-APIs/master/LICENSE",
				Collections.emptyList()
		);
	}
}

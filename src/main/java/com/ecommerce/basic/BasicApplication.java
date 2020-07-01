package com.ecommerce.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
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

		//Authorization header is necessary for query to execute through swagger-ui.html
		Parameter authorizationParameter =
				new ParameterBuilder().name("Authorization")
						.modelRef(new ModelRef("string"))
						.parameterType("header")
						.required(false).build();

		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(PathSelectors.ant("/api/**"))
				.apis(RequestHandlerSelectors.basePackage("com.ecommerce.basic"))
				.build()
				.apiInfo(apiDetails())
				.globalOperationParameters(Collections.singletonList(authorizationParameter));
	}

	private ApiInfo apiDetails(){
		return new ApiInfo(
				"Basic-e-commerce-rest-APIs",
				"Application is providing full backend support for general ecommerce web/mobile app where client" +
						" required to have REST APIs ranging from user management, browsing categories/products, " +
						"cart management, order management to checkout.\n\n" +
						"To execute UserResource/AdminResource apis directly from here you'll need to supply mandatory JWT token. " +
						"Get the JWT token from sign-up/authenticate apis and then put in Authorization field in this format \"Bearer_{YOUR_JWT_TOKEN}\". " +
						"This application uses JWT based authentication/authorization for each request.\n" +
						"For source code, please visit https://github.com/shrikant7/Basic-e-commerce-rest-APIs",
				"1.0",
				"https://raw.githubusercontent.com/shrikant7/Basic-e-commerce-rest-APIs/master/LICENSE",
				new Contact("Shrikant Sharma","https://www.linkedin.com/in/shrikant007/", "shrikant.lnmiit@gmail.com"),
				"MIT License",
				"https://raw.githubusercontent.com/shrikant7/Basic-e-commerce-rest-APIs/master/LICENSE",
				Collections.emptyList()
		);
	}
}

package com.ecommerce.basic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Shrikant Sharma
 */

@Controller
public class ViewController {

	@GetMapping("/home")
	public String homePage() {
		return "home";
	}

	@GetMapping("/category")
	public String createCategory(){
		return "category";
	}
}

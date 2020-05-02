package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Highlight;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.HighlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Shrikant Sharma
 */

@Service
public class HighlightService {
	@Autowired
	ProductService productService;
	@Autowired
	HighlightRepository highlightRepository;

	public List<Highlight> getAllHighlights(){
		return highlightRepository.findAll();
	}

	public Highlight getHighlightById(int highlightId) {
		Optional<Highlight> optionalHighlight = highlightRepository.findById(highlightId);
		optionalHighlight.orElseThrow(() -> new NoSuchResourceException(HighlightService.class, "No Highlight found for highlightId: "+highlightId));
		return optionalHighlight.get();
	}

	public Highlight createHighlight(int productId) {
		Product product = productService.getProductById(productId);
		Highlight highlight = new Highlight().setHighlightedProduct(product);
		return highlightRepository.save(highlight);
	}

	public Highlight deleteHighlight(int highlightId) {
		Highlight highlight = getHighlightById(highlightId);
		highlightRepository.delete(highlight);
		return highlight;
	}
}

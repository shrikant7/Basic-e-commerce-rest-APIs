package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Highlight;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.HighlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		List<Highlight> highlights = highlightRepository.findAll();
		return refreshHighlights(highlights);
	}

	// delete all Highlights which linked to deleted Product
	private List<Highlight> refreshHighlights(List<Highlight> highlights) {
		List<Highlight> deletableHighlights = highlights.stream().filter(h -> h.getHighlightedProduct().isDeleted()).collect(Collectors.toList());
		highlightRepository.deleteAll(deletableHighlights);
		highlights.removeAll(deletableHighlights);
		return highlights;
	}

	public Highlight getHighlightById(long highlightId) {
		Optional<Highlight> optionalHighlight = highlightRepository.findById(highlightId);
		optionalHighlight.orElseThrow(() -> new NoSuchResourceException(HighlightService.class, "No Highlight found for highlightId: "+highlightId));
		return optionalHighlight.get();
	}

	public Highlight createHighlight(long productId) {
		Product product = productService.getProductById(productId);
		Highlight highlight = new Highlight().setHighlightedProduct(product);
		return highlightRepository.save(highlight);
	}

	public Highlight deleteHighlight(long highlightId) {
		Highlight highlight = getHighlightById(highlightId);
		highlightRepository.delete(highlight);
		return highlight;
	}

	public void deleteProductFromAnyHighlight(Product product) {
		int deletedHighlight = highlightRepository.deleteByHighlightedProduct(product);
		//System.err.println("deletedHighlight: "+deletedHighlight);
	}
}

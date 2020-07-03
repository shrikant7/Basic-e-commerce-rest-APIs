package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.exceptions.UniqueKeyViolationException;
import com.ecommerce.basic.models.Highlight;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.HighlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.NO_HIGHLIGHT_ID_EXCEPTION;
import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.UNIQUE_HIGHLIGHT_EXCEPTION;

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
		optionalHighlight.orElseThrow(() -> new NoSuchResourceException(NO_HIGHLIGHT_ID_EXCEPTION, "No Highlight found for highlightId: "+highlightId));
		return optionalHighlight.get();
	}

	public Highlight createHighlight(long productId) {
		Product product = productService.getProductById(productId);
		Highlight highlight = new Highlight().setHighlightedProduct(product);
		try {
			return highlightRepository.save(highlight);
		} catch(Exception e) {
			throw new UniqueKeyViolationException(UNIQUE_HIGHLIGHT_EXCEPTION, "Product already exist in highlight");
		}
	}

	public Highlight deleteHighlight(long highlightId) {
		Highlight highlight = getHighlightById(highlightId);
		highlightRepository.delete(highlight);
		return highlight;
	}

	public void deleteProductsFromAnyHighlight(List<Product> product) {
		int deletedHighlight = highlightRepository.deleteHighlightsHaveProducts(product);
		//System.err.println("deletedHighlights: "+deletedHighlight);
	}
}

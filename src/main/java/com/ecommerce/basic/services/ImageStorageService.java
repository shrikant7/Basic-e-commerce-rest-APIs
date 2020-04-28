package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Shrikant Sharma
 */

@Service
public class ImageStorageService {
	private final Path imageStoragePath;

	public ImageStorageService() {
		this.imageStoragePath = Paths.get("./uploads").toAbsolutePath().normalize();
		try {
			Files.createDirectories(imageStoragePath);
		} catch (Exception e) {
			throw new FileStorageException("Could not able to create directory to upload images",e);
		}
	}


	public String storeImage(MultipartFile productImage) {
		String originalImageName = productImage.getOriginalFilename();
		if(!originalImageName.endsWith(".jpg") && !originalImageName.endsWith(".jpeg") && !originalImageName.endsWith(".png")){
			throw new FileStorageException("Sorry!! file is of INVALID extension");
		}

		try {
			String newImageName = System.currentTimeMillis()+"_"+productImage.getOriginalFilename();
			Path targetLocation = imageStoragePath.resolve(newImageName);
			Files.copy(productImage.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return newImageName;
		} catch (Exception e) {
			throw new FileStorageException("Could not store image. Please try again",e);
		}
	}
}

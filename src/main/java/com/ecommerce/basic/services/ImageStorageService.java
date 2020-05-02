package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
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


	public String storeImage(String categoryName, MultipartFile productImage) {
		String originalImageName = productImage.getOriginalFilename();
		String[] nameSplit = originalImageName.split("[.]");
		if(nameSplit.length !=2 || (!originalImageName.endsWith(".jpg")
				&& !originalImageName.endsWith(".jpeg") && !originalImageName.endsWith(".png"))) {
				throw new FileStorageException("Sorry!! file is of INVALID name or extension: "+originalImageName);
		}

		try {
			Path targetDirectory = imageStoragePath.resolve(categoryName);
			Files.createDirectories(targetDirectory);

			String newImageName = categoryName+"_"+System.currentTimeMillis()+"."+nameSplit[1];
			Path targetLocation = targetDirectory.resolve(newImageName);
			Files.copy(productImage.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return newImageName;
		} catch (Exception e) {
			throw new FileStorageException("Could not store image. Please try again",e);
		}
	}

	public Resource loadImageAsResource(String imageName) {
		try {
			String categoryName = imageName.split("_")[0];
			Path imagePath = imageStoragePath.resolve(categoryName+"/"+imageName).normalize();
			Resource resource = null;
			resource = new UrlResource(imagePath.toUri());
			if(resource.exists()){
				return resource;
			}else {
				throw new FileStorageException("image not found");
			}
		} catch (MalformedURLException e) {
			throw new FileStorageException("error in retrieving image",e);
		}
	}
}

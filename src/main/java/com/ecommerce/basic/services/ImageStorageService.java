package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.FileStorageException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

/**
 * @author Shrikant Sharma
 */

@Service
public class ImageStorageService {
	@Value("${gcs-resource-bucket}")
	private String bucket;
	@Value("${gcs-resource-path}")
	private String cloudStoragePath;
	@Autowired
	Storage storage;
	@Autowired
	private ApplicationContext context;

	public String storeImage(int categoryId, MultipartFile productImage) {
		String originalImageName = productImage.getOriginalFilename();
		String[] nameSplit = originalImageName.split("[.]");
		if (nameSplit.length != 2 || (!originalImageName.endsWith(".jpg")
				&& !originalImageName.endsWith(".jpeg") && !originalImageName.endsWith(".png"))) {
			throw new FileStorageException("Sorry!! file is of INVALID name or extension: " + originalImageName);
		}

		try {
			String newImageName = categoryId + "_" + System.currentTimeMillis() + "." + nameSplit[1];
			String targetPath = cloudStoragePath + "/" + categoryId + "/" + newImageName;
			Resource gcsImage = context.getResource(targetPath);
			byte[] pictureBytes = StreamUtils.copyToByteArray(productImage.getInputStream());
			try (OutputStream os = ((WritableResource) gcsImage).getOutputStream()) {
				os.write(pictureBytes);
			}
			return newImageName;
		} catch (Exception e) {
			throw new FileStorageException("Could not store image. Please try again", e);
		}
	}

	public Resource loadImageAsResource(String imageName) {
		String categoryId = imageName.split("_")[0];
		String imagePath = cloudStoragePath + "/" + categoryId + "/" + imageName;
		Resource resource = context.getResource(imagePath);
		if (resource.exists()) {
			return resource;
		} else {
			throw new FileStorageException("image not found");
		}
	}

	@SneakyThrows
	public String moveImage(String imageName, int newCategoryId) {
		String[] split = imageName.split("_");
		String categoryId = split[0];
		String extension = split[1].split("[.]")[1];
		String newImageName = newCategoryId + "_" + System.currentTimeMillis() + "." + extension;

		String imagePath = cloudStoragePath + "/" + categoryId + "/" + imageName;
		GoogleStorageResource resource = (GoogleStorageResource) context.getResource(imagePath);
		resource.getBlob().copyTo(String.valueOf(newCategoryId), newImageName);
		return newImageName;
	}

	@SneakyThrows
	public void deleteImage(String imageName) {
		String[] split = imageName.split("_");
		String categoryId = split[0];
		String imagePath = cloudStoragePath + "/" + categoryId + "/" + imageName;
		GoogleStorageResource resource = (GoogleStorageResource) context.getResource(imagePath);
		resource.getBlob().delete();
	}

	public void deleteAllImages(int categoryId) {
		Iterable<Blob> blobs = storage.list(bucket, Storage.BlobListOption.currentDirectory(),
								Storage.BlobListOption.prefix(categoryId+"/")).iterateAll();
		for (Blob blob : blobs) {
			blob.delete();
		}
	}
}

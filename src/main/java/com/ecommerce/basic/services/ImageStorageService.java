package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.FileStorageException;
import com.ecommerce.basic.exceptions.InvalidFileExtension;
import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.CopyWriter;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.OutputStream;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.*;

/**
 * @author Shrikant Sharma
 */

@Service
public class ImageStorageService {
	@Value("${gcs-resource-bucket}")
	private String bucket;
	@Value("${gcs-resource-path}")
	private String cloudStoragePath;
	@Value("${host-address}")
	private String hostAddress;
	@Autowired
	Storage storage;
	@Autowired
	private ApplicationContext context;

	public String storeImage(long categoryId, MultipartFile productImage) {
		String originalImageName = productImage.getOriginalFilename();
		String[] nameSplit = originalImageName.split("[.]");
		if (nameSplit.length != 2 || (!originalImageName.endsWith(".jpg")
				&& !originalImageName.endsWith(".jpeg") && !originalImageName.endsWith(".png"))) {
			throw new InvalidFileExtension(INVALID_IMAGE_EXTENSION, "INVALID name/extension of Image: " + originalImageName);
		}

		try {
			String newImageName = categoryId + "_" + System.currentTimeMillis() + "." + nameSplit[1];
			String targetPath = cloudStoragePath + "/" + categoryId + "/" + newImageName;
			Resource gcsImage = context.getResource(targetPath);
			byte[] pictureBytes = StreamUtils.copyToByteArray(productImage.getInputStream());
			try (OutputStream os = ((WritableResource) gcsImage).getOutputStream()) {
				os.write(pictureBytes);
			}
			return ServletUriComponentsBuilder.fromHttpUrl(hostAddress)
					.path("api/downloadImage/")
					.path(newImageName).toUriString();
		} catch (Exception e) {
			throw new FileStorageException(FILE_STORAGE_EXCEPTION, "Could not store image. Please try again", e);
		}
	}

	public Resource loadImageAsResource(String imageName) {
		String categoryId = imageName.split("_")[0];
		String imagePath = cloudStoragePath + "/" + categoryId + "/" + imageName;
		Resource resource = context.getResource(imagePath);
		if (resource.exists()) {
			return resource;
		} else {
			throw new NoSuchResourceException(NO_IMAGE_IN_CATEGORY_EXCEPTION,"Image not found for categoryId: "+categoryId);
		}
	}

	@SneakyThrows
	public String moveImage(String imageUri, long newCategoryId) {
		String[] split = imageUri.split("/");
		String imageName = split[split.length - 1];
		split = imageName.split("_");
		String categoryId = split[0];
		String extension = split[1].split("[.]")[1];
		String newImageName = newCategoryId + "_" + System.currentTimeMillis() + "." + extension;

		String imagePath = cloudStoragePath + "/" + categoryId + "/" + imageName;
		GoogleStorageResource resource = (GoogleStorageResource) context.getResource(imagePath);
		Blob blob = resource.getBlob();
		CopyWriter copyWriter = blob.copyTo(bucket, newCategoryId+"/"+newImageName);
		Blob copiedBlob = copyWriter.getResult();
   		boolean deleted = blob.delete();
		return ServletUriComponentsBuilder.fromHttpUrl(hostAddress)
				.path("api/downloadImage/")
				.path(newImageName).toUriString();
	}

	@SneakyThrows
	public void deleteImage(String imageURI) {
		Runnable runnable = ()-> {
			try {
				String[] split = imageURI.split("/");
				String imageName = split[split.length - 1];
				split = imageName.split("_");
				String categoryId = split[0];
				String imagePath = cloudStoragePath + "/" + categoryId + "/" + imageName;
				GoogleStorageResource resource = (GoogleStorageResource) context.getResource(imagePath);
				resource.getBlob().delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		new Thread(runnable).start();
	}

	public void deleteAllImages(int categoryId) {
		Runnable runnable = () -> {
			Iterable<Blob> blobs = storage.list(bucket, Storage.BlobListOption.currentDirectory(),
					Storage.BlobListOption.prefix(categoryId + "/")).iterateAll();
			for (Blob blob : blobs) {
				blob.delete();
			}
		};
		new Thread(runnable).start();
	}
}

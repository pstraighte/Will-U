package com.beteam.willu.common.s3;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {

	private final AmazonS3 s3Client; // AmazonS3 클라이언트 주입

	private static final String BUCKET_NAME = "willu-bucket"; // 생성한 버킷 이름
	private final UserRepository userRepository;

	public S3Service(AmazonS3 s3Client, UserRepository userRepository) {
		this.s3Client = s3Client; // AmazonS3 클라이언트 주입
		this.userRepository = userRepository;
	}

	public String uploadImage(MultipartFile imageFile) throws IOException {
		String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();  // 고유한 파일 이름 생성
		String objectKey = "profile-images/" + uniqueFileName;  // S3에 저장할 객체 키 생성

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(imageFile.getSize());  // 파일 크기 설정

		s3Client.putObject(BUCKET_NAME, objectKey, imageFile.getInputStream(), metadata);  // S3에 이미지 업로드

		log.info(objectKey);
		return objectKey; // 업로드된 이미지의 S3 오브젝트 키를 반환
	}

	public void generateImageUrl(String objectKey, User user) {
		String bucketBaseUrl = "https://willu-bucket.s3.ap-northeast-2.amazonaws.com"; // Replace with your bucket URL
		user.setPicture(bucketBaseUrl + "/" + objectKey);
		userRepository.save(user);
	}
}

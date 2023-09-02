package com.beteam.willu.common.s3;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.beteam.willu.common.security.UserDetailsImpl;

@RestController
@RequestMapping("/api")
public class S3Controller {

	private final S3Service s3Service;  // S3 서비스 주입

	@Autowired
	public S3Controller(S3Service s3Service) {
		this.s3Service = s3Service;  // 생성자를 통해 S3 서비스 주입
	}

	@PostMapping("/image")
	@Transactional
	public void uploadImage(@RequestParam("file") MultipartFile imageFile,
		@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		String objectKey = s3Service.uploadImage(imageFile);  // S3 서비스를 통해 이미지 업로드 수행
		s3Service.generateImageUrl(objectKey, userDetails.getUser());  // 이미지 URL 반환	}
	}
}


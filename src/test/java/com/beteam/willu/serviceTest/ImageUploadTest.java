package com.beteam.willu.serviceTest;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.beteam.willu.common.s3.S3Service;
import com.beteam.willu.user.repository.UserRepository;

@DisplayName("S3 TEST")
@ExtendWith(MockitoExtension.class)
public class ImageUploadTest {
	@InjectMocks
	private S3Service s3Service;

	@Spy
	private AmazonS3 s3Client;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testUploadImage() throws IOException {
		MultipartFile imageFile = Mockito.mock(MultipartFile.class);
		when(imageFile.getOriginalFilename()).thenReturn("test.jpg");
		when(imageFile.getSize()).thenReturn(1024L); // 파일 크기 (바이트)

		InputStream inputStream = mock(InputStream.class);
		when(imageFile.getInputStream()).thenReturn(inputStream);

		// 테스트할 메서드를 호출합니다.
		String objectKey = s3Service.uploadImage(imageFile);

		Assertions.assertThat(objectKey).isNotNull();
		Assertions.assertThat(objectKey).contains("test.jpg");

	}
}

package com.beteam.willu.common.s3;

import com.beteam.willu.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "S3 API", description = "프로필 이미지 관련 API")
@RestController
@RequestMapping("/api")
public class S3Controller {

    private final S3Service s3Service;  // S3 서비스 주입

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;  // 생성자를 통해 S3 서비스 주입
    }

    @Operation(summary = "프로필 이미지 제어", description = "S3 저장소에 이미지를 업로드하고 업로드한 이미지를 반환해 사용자 프로필 이미지로 보여준다.")
    @PostMapping("/image")
    @Transactional
    public void uploadImage(@RequestParam("file") MultipartFile imageFile,
                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        String objectKey = s3Service.uploadImage(imageFile);  // S3 서비스를 통해 이미지 업로드 수행
        s3Service.generateImageUrl(objectKey, userDetails.getUser());  // 이미지 URL 반환	}
    }
}


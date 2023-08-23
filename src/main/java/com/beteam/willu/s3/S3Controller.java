package com.beteam.willu.s3;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class S3Controller {

    private final S3Service s3Service;  // S3 서비스 주입

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;  // 생성자를 통해 S3 서비스 주입
    }

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file") MultipartFile imageFile) throws IOException {
        String objectKey = s3Service.uploadImage(imageFile);  // S3 서비스를 통해 이미지 업로드 수행
        return "Profile image uploaded. Object key: " + objectKey;  // 업로드 결과 반환
    }
}

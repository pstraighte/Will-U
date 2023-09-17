package com.beteam.willu.hashtag.controller;

import com.beteam.willu.hashtag.dto.TagTopResponseDto;
import com.beteam.willu.hashtag.service.HashTagService;
import com.beteam.willu.post.dto.PostResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "HashTag API", description = "게시글 태크관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HashTagController {
    private final HashTagService hashTagService;

    //태그 검색
    @Transactional
    @GetMapping("/tag/search")
    public ResponseEntity<Page<PostResponseDto>> createTag(
            @RequestParam("content") String content,
            @RequestParam("recruitmentTag") boolean recruitmentTag,
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 파라미터 (기본값: 0)
            @RequestParam(value = "size", defaultValue = "10") int size // 페이지당 항목 수 파라미터 (기본값: 10)
    ) {
        if (content.length() < 2) {
            // 검색어 길이가 2자 미만일 경우 에러 응답을 반환하거나, 다른 처리를 할 수 있습니다.
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size); // 페이지와 항목 수를 기반으로 페이징 정보 생성
        Page<PostResponseDto> searchResultPage = hashTagService.createTag(content, recruitmentTag, pageable);
        return ResponseEntity.ok().body(searchResultPage);// 서비스를 통해 검색 실행
    }

    //태그 중 top 5 선정
    @Transactional
    @GetMapping("/tag")
    public List<TagTopResponseDto> getTags() {
        return hashTagService.getTags();
    }

}

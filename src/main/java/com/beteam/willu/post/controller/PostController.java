package com.beteam.willu.post.controller;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.post.dto.PostRequestDto;
import com.beteam.willu.post.dto.PostResponseDto;
import com.beteam.willu.post.service.PostServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post API", description = "게시글 제어 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostServiceImpl postService;

    // 게시글 작성
    @Operation(summary = "게시글 생성", description = "정해진 파라미터를 받은 후 게시글 데이터를 생성합니다.")
    @Parameter(name = "title", required = true, schema = @Schema(type = "String"), description = "게시글 제목")
    @Parameter(name = "content", required = true, schema = @Schema(type = "String"), description = "게시글 내용")
    @Parameter(name = "promiseTime", required = true, schema = @Schema(type = "LocalDateTime", format = "date"), description = "약속 시간 - 게시글의 활성화 필드를 제어 할 정보")
    @Parameter(name = "promiseArea", required = true, schema = @Schema(type = "String"), description = "지역")
    @Parameter(name = "maxnum", required = true, schema = @Schema(type = "Long"), description = "게시글 최대 모집인원 수")
    @Parameter(name = "category", required = true, schema = @Schema(type = "string"), description = "게시글 분류")
    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto postRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResponseDto post = postService.createPost(postRequestDto, userDetails.getUser());
        return ResponseEntity.status(201).body(post);
    }

    @Operation(summary = "게시글 전체 조회", description = "게시글의 목록을 페이징에 관련된 정보와 함께 조회 합니다.")
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDto>> getPosts(
            @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호 파라미터 (기본값: 0)
            @RequestParam(value = "size", defaultValue = "10") int size // 페이지당 항목 수 파라미터 (기본값: 10)
    ) {
        Pageable pageable = PageRequest.of(page, size); // 페이지와 항목 수를 기반으로 페이징 정보 생성
        Page<PostResponseDto> posts = postService.getPosts(pageable);
        return ResponseEntity.ok().body(posts); // 게시글 목록 view 이름 (html)
    }

    // 게시글 상세 조회
    @Operation(summary = "게시글 개별 조회", description = "PathVariable 형태의 id 값을 이용해 id에 해당하는 게시글의 데이터를 조회한다.")
    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    //
    //게시글 수정
    @Operation(summary = "게시글 수정", description = "PathVariable 형태의 id 값을 이용해 id에 해당하는 게시글의 데이터를 수정한다.")
    @Parameter(name = "title", schema = @Schema(type = "String"), description = "게시글 제목")
    @Parameter(name = "content", schema = @Schema(type = "String"), description = "게시글 내용")
    @Parameter(name = "promiseTime", schema = @Schema(type = "LocalDateTime", format = "date"), description = "약속 시간 - 게시글의 활성화 필드를 제어 할 정보")
    @Parameter(name = "promiseArea", schema = @Schema(type = "String"), description = "지역")
    @Parameter(name = "maxnum", schema = @Schema(type = "Long"), description = "게시글 최대 모집인원 수")
    @Parameter(name = "category", schema = @Schema(type = "string"), description = "게시글 분류")
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id,
                                                      @Valid @RequestBody PostRequestDto postRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deleteOldTagMap(id);
        PostResponseDto result = postService.updatePost(id, postRequestDto, userDetails.getUsername());
        return ResponseEntity.status((HttpStatus.OK)).body(result);
    }

    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "PathVariable 형태의 id 값을 이용해 id에 해당하는 게시글의 데이터를 삭제한다.")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long id,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(id, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("게시글이 삭제되었습니다", 200));
    }

    // 모집완료 -> 모집중
    @Operation(summary = "모집완료 버튼 제어 (모집완료 -> 모집중)", description = "ResponseEntity의 상태값을 이용해 모집완료 버튼을 제어한다.")
    @PatchMapping("/posts/{id}/activate-recruitment")
    public ResponseEntity<ApiResponseDto> activateRecruitment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long id) {
        postService.activateRecruitment(id, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("모집중으로 변경", 200));
    }

    // 모집중 -> 모집완료
    @Operation(summary = "모집완료 버튼 제어 (모집중 -> 모집완료중)", description = "ResponseEntity의 상태값을 이용해 모집완료 버튼을 제어한다.")
    @PatchMapping("/posts/{id}/complete-recruitment")
    public ResponseEntity<ApiResponseDto> completeRecruitment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long id) {
        postService.completeRecruitment(id, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto("모집완료", 200));
    }

    // 게시글 검색
    // TODO 파라매터 받아오는 방법 프론트엔드와 연결 시 확정 가능할 듯
    // size를 선택할 수 있는 기능이 필요한가? ex) 10개, 20개, 30개
    @GetMapping("/search/posts")
    public ResponseEntity<Page<PostResponseDto>> searchPosts(
            @RequestParam("keyword") String keyword,
            @RequestParam("criteria") String criteria,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "recruitment", defaultValue = "false") boolean recruitment
    ) {
        // 정렬 방식 지정 (id 필드를 내림차순으로 정렬)
        Sort sort = Sort.by(Sort.Order.desc("id"));

        Pageable pageable = PageRequest.of(page, size, sort); // 페이지와 항목 수를 기반으로 페이징 정보 생성
        if (keyword.length() < 2) {
            // 검색어 길이가 2자 미만일 경우 에러 응답을 반환하거나, 다른 처리를 할 수 있습니다.
            if (keyword.isEmpty()) {
                return ResponseEntity.ok().body(postService.getPosts(pageable));
            }
            return ResponseEntity.badRequest().build();
        }
        Page<PostResponseDto> searchResultPage = postService.searchPosts(keyword, criteria, recruitment,
                pageable); // 서비스를 통해 검색 실행
        return ResponseEntity.ok().body(searchResultPage); // 검색 결과를 응답에 담아 반환
    }


}

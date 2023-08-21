package com.beteam.willu.post;

import com.beteam.willu.common.ApiResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResponseDto post = postService.createPost(postRequestDto, userDetails.getUser());
        return ResponseEntity.status(201).body(post);
    }

    // 게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getPosts() {
        List<PostResponseDto> result = postService.getPosts();
        return ResponseEntity.ok().body(result);
    }

//    @GetMapping("/posts")
//    public String getPosts(Pageable pageable, Model model) {
//        Page<PostResponseDto> postPage = postService.getPosts(pageable);
//        model.addAttribute("postPage", postPage);
//        return null; // 게시글 목록 view 이름 (html)
//    }

    // 게시글 상세 조회
    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    //
    //게시글 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResponseDto result = postService.updatePost(id, postRequestDto, userDetails.getUsername());
        return ResponseEntity.status((HttpStatus.OK)).body(result);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long id,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            postService.deletePost(id, userDetails.getUser());

            return ResponseEntity.ok().body(new ApiResponseDto("게시글이 삭제되었습니다", 200));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
        }
    }

    // 모집완료 -> 모집중
    @PatchMapping("/posts/{id}/activate-recruitment")
    public ResponseEntity<ApiResponseDto> activateRecruitment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long id) {
        try {
            postService.activateRecruitment(id, userDetails.getUser());
            return ResponseEntity.ok().body(new ApiResponseDto("모집중으로 변경", 200));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
        } catch (RecruitmentStatusException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("이미 모집중입니다.", 400));
        }
    }

    // 모집중 -> 모집완료
    @PatchMapping("/posts/{id}/complete-recruitment")
    public ResponseEntity<ApiResponseDto> completeRecruitment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long id) {
        try {
            postService.completeRecruitment(id, userDetails.getUser());
            return ResponseEntity.ok().body(new ApiResponseDto("모집완료", 200));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
        } catch (RecruitmentStatusException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("이미 모집완료 되었습니다.", 400));
        }
    }
}
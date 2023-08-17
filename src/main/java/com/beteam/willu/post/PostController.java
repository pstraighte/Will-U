package com.beteam.willu.post;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.ui.Model;
import org.springframework.security.core.userdetails.UserDetails;
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
                                                      @AuthenticationPrincipal UserDetails userDetails){
            PostResponseDto result = postService.updatePost(id, postRequestDto, userDetails.getUsername());
            return ResponseEntity.status((HttpStatus.OK)).body(result);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponseDto> deletePost(@PathVariable Long id,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
           postService.deletePost(id, userDetails.getUser());
            return ResponseEntity.ok().body(new ApiResponseDto("게시글이 삭제되었습니다",200));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto("게시글을 찾을 수 없습니다.", 400));
        }
    }
    //모집한 활동 조회 >> user id가 갖고 있는 post 갯수 및 내용 반환
//    @GetMapping("/posts/hangout/{id}")
//
//    public ResponseEntity

    //참여한 활동 조회

    //모집 완료
}
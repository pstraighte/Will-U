package com.beteam.willu.stomp.controller;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.stomp.dto.ChatRoomNameResponseDto;
import com.beteam.willu.stomp.dto.ChatRoomsResponseDto;
import com.beteam.willu.stomp.dto.ChatSaveRequestDto;
import com.beteam.willu.stomp.dto.ChatsResponseDto;
import com.beteam.willu.stomp.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 게시물이 생성되었을때 채팅룸 생성 (확인 완료)
    @PostMapping("/chat/posts/{id}/creatRoom")
    public void createRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.createRoom(id, userDetails);
    }

    // 사용자가 속한 채팅방(모두) 불러오기 (확인 완료) (프론트 적용 완료)
    // 채팅방 비활성화 (채팅방 저장 테이터중 활성화 필드가 true인 값만 가져온다.)
    @GetMapping("/chat/users/{id}")
    public ChatRoomsResponseDto getRooms(@PathVariable Long id) {
        return chatRoomService.getRooms(id);
    }

    // 쿠키에 저장된 사용자 이름으로 사용자 id 가져오기 (확인 완료) (프론트 적용 완료)
    // 채팅방에 유저 id 엔티티가 없음 (수정 가능성 있음)
    @GetMapping("/chat/getUsers/{id}")
    public Long getUser(@PathVariable String id) {
        return chatRoomService.getUser(id);
    }

    // 특정 채팅룸 불러오기 (채팅방의 활성화 확인)
    @GetMapping("/chat/chatRooms/{id}")
    public ChatRoomNameResponseDto getRoom(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getRoom(id, userDetails);
    }

    // 생성된 채팅 저장 (확인 완료) (프론트 적용 완료)
    @PostMapping("/chat/chatRooms")
    public void createRooms(@RequestBody ChatSaveRequestDto chatSaveRequestDto) {
        chatRoomService.createRooms(chatSaveRequestDto);
    }

    // 특정 채팅방 채팅 조회 (확인 완료) (프론트 적용 완료)
    @GetMapping("/chat/chatRoom/{id}")
    public ChatsResponseDto getChat(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getChat(id, userDetails);
    }

    //(할것)
    // 게시글에서 신청 버튼 클릭시 사용자 채팅방 추가
    @PostMapping("chat/join/{postId}/{chatRoomId}")
    public void userJoin(@PathVariable long postId, @PathVariable long chatRoomId,
                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.userJoin(postId, chatRoomId, userDetails);
    }

    // 사용자 채팅방에서 다른사용자 추방 (ADMIN 용) (프론트 적용 완료)
    // 특청 사용자의 id를 이용해 테이블에서 유저 삭제
    // 강퇴된 유저가 다시 채팅방에 신청 했을 때 생각하기 (미구현)
    @DeleteMapping("/chat/users/{userId}/chatRoom/{chatId}/kickout")
    public void kickoutUser(@PathVariable(name = "userId") Long userId, @PathVariable Long chatId,
                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.kickoutUser(userId, chatId, userDetails);
    }

    // 사용자 채팅방 나가기 (USER 용) -> (인원이 있을때 방장이 나갔다 / 채팅방에 사람이 아예 없을때)
    // (프론트 적용 완료)
    @DeleteMapping("/chat/chatRoom/{id}/out")
    public void outUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.outUser(id, userDetails);
    }

    // 채팅방 사용자들 조회 (프론트 적용 완료)
    // 채팅방 id를 사용해서 조회
    @GetMapping("/chat/chatRooms/{id}/users")
    public ChatRoomsResponseDto getChatRoomUsers(@PathVariable Long id) {
        return chatRoomService.getChatRoomUsers(id);
    }

}

package com.beteam.willu.stomp;

import com.beteam.willu.security.UserDetailsImpl;
import com.beteam.willu.stomp.dto.ChatRoomsResponseDto;
import com.beteam.willu.stomp.dto.ChatSaveRequestDto;
import com.beteam.willu.stomp.dto.ChatsResponseDto;
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

    // 사용자가 속한 채팅방(모두) 불러오기 (확인 완료)
    @GetMapping("/chat/users/{id}")
    public ChatRoomsResponseDto getRooms(@PathVariable Long id) {
        return chatRoomService.getRooms(id);
    }

    // 쿠키에 저장된 사용자 이름으로 사용자 id 가져오기 (확인 완료)
    // 채팅방에 유저 id 엔티티가 없음 (수정 가능성 있음)
    @GetMapping("/chat/getUsers/{id}")
    public Long getUser(@PathVariable String id) {
        return chatRoomService.getUser(id);
    }

//    // 특정 채팅룸 불러오기
//    @GetMapping("/chat/chatRooms/{id}")
//    public ChatRoomNameResponseDto getRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return chatRoomService.getRoom(id, userDetails);
//    }

    // 생성된 채팅 저장 (확인 완료)
    @PostMapping("/chat/chatRooms")
    public void createRooms(@RequestBody ChatSaveRequestDto chatSaveRequestDto) {
        chatRoomService.createRooms(chatSaveRequestDto);
    }

    // 특정 채팅방 채팅 조회 (확인 완료)
    @GetMapping("/chat/chatRoom/{id}")
    public ChatsResponseDto getChat(@PathVariable Long id) {
        return chatRoomService.getChat(id);
    }

    //(할것)
    // 게시글에서 신청 버튼 클릭시 사용자 채팅방 추가

    // 사용자 채팅방에서 다른사용자 추방 (ADMIN 용)

    // 사용자 채팅방 나가기 (USER 용) -> 인원이 있을때 방장이 나갔다 / 채팅방에 사람이 아예 없을때

    
    // 채팅방 비활성화
}

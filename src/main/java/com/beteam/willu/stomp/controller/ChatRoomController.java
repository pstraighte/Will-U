package com.beteam.willu.stomp.controller;

import com.beteam.willu.common.ApiResponseDto;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.notification.service.NotificationService;
import com.beteam.willu.stomp.dto.*;
import com.beteam.willu.stomp.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ChatRoom API", description = "채팅방 제어 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;

    // 게시물이 생성되었을때 채팅룸 생성 (확인 완료)
    @Operation(summary = "채팅방 생성", description = "PathVariable형태의 게시글 id를 이용해 채팅방 데이터를 생성하고 로그인한 사용자의 데이터를 이용해 채팅방에 어떤 사용자가 있는지 확인하는 데이터를 생성한다.")
    @PostMapping("/chat/posts/{id}/creatRoom")
    public void createRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.createRoom(id, userDetails);
    }

    // 사용자가 속한 채팅방(모두) 불러오기 (확인 완료) (프론트 적용 완료)
    // 채팅방 비활성화 (채팅방 저장 테이터중 활성화 필드가 true인 값만 가져온다.)
    @Operation(summary = "채팅방 조회", description = "PathVariabl형태의 사용자 id를 이용해 사용자가 속한 채팅방을 조회한다(채팅방 활성화 필드가 true인 것만 조회)")
    @GetMapping("/chat/users/{id}")
    public ChatRoomsResponseDto getRooms(@PathVariable Long id) {
        return chatRoomService.getRooms(id);
    }

    // 쿠키에 저장된 사용자 이름으로 사용자 id 가져오기 (확인 완료) (프론트 적용 완료)
    // 채팅방에 유저 id 엔티티가 없음 (수정 가능성 있음)
    @Operation(summary = "사용자 ID 조회", description = "사용자의 닉네임을 이용해 사용자의 ID를 조회한다.")
    @GetMapping("/chat/getUsers/{id}")
    public Long getUser(@PathVariable String id) {
        return chatRoomService.getUser(id);
    }

    // 특정 채팅룸 불러오기 (채팅방의 활성화 확인)
    @Operation(summary = "특정 채팅방 조회", description = "사용자가 선택한 채팅방의 내용을 조회한다. (채팅방 제목을 나타내기 위해서)")
    @GetMapping("/chat/chatRooms/{id}")
    public ChatRoomNameResponseDto getRoom(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getRoom(id, userDetails);
    }

    // 생성된 채팅 저장 (확인 완료) (프론트 적용 완료)
    @Operation(summary = "채팅 저장", description = "정해진 파라미터를 받은 후 채팅을 저장합니다.")
    @Parameter(name = "userId", required = true, schema = @Schema(type = "String"), description = "사용자 아이디")
    @Parameter(name = "chatContent", required = true, schema = @Schema(type = "String"), description = "채팅 내용")
    @Parameter(name = "roomId", required = true, schema = @Schema(type = "Long"), description = "채팅방 아이디")
    @Parameter(name = "createdAt", required = true, schema = @Schema(type = "LocalDateTime", format = "date"), description = "지역")
    @PostMapping("/chat/chatRooms")
    public void createRooms(@Valid @RequestBody ChatSaveRequestDto chatSaveRequestDto) {
        chatRoomService.createRooms(chatSaveRequestDto);
    }

    // 특정 채팅방 채팅 조회 (확인 완료) (프론트 적용 완료)
    @Operation(summary = "채팅 조회", description = "PathVariable형태의 채팅방 id를 이용해 특정 채팅방에 해당하는 채팅들을 조회한다.")
    @GetMapping("/chat/chatRoom/{id}")
    public ChatsResponseDto getChat(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.getChat(id, userDetails);
    }

    //(할것)
    // 로그인한 유저의 채팅방 추가(사용할 일 없을지도..!)
    @Operation(summary = "사용자 채팅방 추가", description = "PathVariable 형태의 채팅방 id값을 이용해 사용자를 채팅방에 추가한다.")
    @PostMapping("chat/join/{postId}/{chatRoomId}")
    public ResponseEntity<ApiResponseDto> userJoin(@PathVariable long postId, @PathVariable long chatRoomId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.userJoin(postId, chatRoomId, userDetails);
        return ResponseEntity.ok().body(new ApiResponseDto("사용자 채팅방 추가 성공", 200));
    }

    //채팅방 참가
    @Operation(summary = "사용자 채팅방 참가", description = "정해진 파라미터 값을 이용해 모집자가 승인 버튼 클릭시 사용자를 채팅방에 추가")
    @Parameter(name = "postId", schema = @Schema(type = "Long"), description = "게시글 아이디")
    @Parameter(name = "userId", schema = @Schema(type = "Long"), description = "유저 아이디")
    @Parameter(name = "notificationId", schema = @Schema(type = "Long"), description = "알림 아이디")
    @PostMapping("/chatRoom/join")
    public void joinUserChatRoom(@RequestBody ChatroomJoinRequestDto requestDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationService.updateRead(requestDto.getNotificationId());
        chatRoomService.joinUserChatRoom(requestDto, userDetails.getUser());
    }

    // 사용자 채팅방에서 다른사용자 추방 (ADMIN 용) (프론트 적용 완료)
    // 특청 사용자의 id를 이용해 테이블에서 유저 삭제
    // 강퇴된 유저가 다시 채팅방에 신청 했을 때 생각하기 (미구현)
    @Operation(summary = "사용자 채팅방 강퇴", description = "PathVariable형태의 사용자 id 와 채팅방 id를 이용해 사용자를 채팅방에서 강퇴한다.")
    @DeleteMapping("/chat/users/{userId}/chatRoom/{chatId}/kickout")
    public void kickoutUser(@PathVariable(name = "userId") Long userId, @PathVariable Long chatId,
                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.kickoutUser(userId, chatId, userDetails);
    }

    // 사용자 채팅방 나가기 (USER 용) -> (인원이 있을때 방장이 나갔다 / 채팅방에 사람이 아예 없을때)
    // (프론트 적용 완료)
    @Operation(summary = "채팅방 나가기", description = "PathVariable형태의 채팅방 id를 이용해 사용자가 해당 채팅방을 나간다.")
    @DeleteMapping("/chat/chatRoom/{id}/out")
    public void outUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatRoomService.outUser(id, userDetails);
    }

    // 채팅방 사용자들 조회 (프론트 적용 완료)
    // 채팅방 id를 사용해서 조회
    @Operation(summary = "채팅방 사용자 목록 조회", description = "PathVariable형태의 채팅방 id를 이용해 해당 채팅방의 사용자 목록을 조회한다.")
    @GetMapping("/chat/chatRooms/{id}/users")
    public ChatRoomsResponseDto getChatRoomUsers(@PathVariable Long id) {
        return chatRoomService.getChatRoomUsers(id);
    }

}

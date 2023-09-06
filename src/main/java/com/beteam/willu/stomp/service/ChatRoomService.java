package com.beteam.willu.stomp.service;

import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.notification.dto.NotificationEvent;
import com.beteam.willu.notification.entity.NotificationType;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.dto.*;
import com.beteam.willu.stomp.entity.Chat;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRepository;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j(topic = "ChatRoomService")
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomsRepository userChatRoomsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 게시물이 생성되었을때 채팅룸 생성 (확인 완료)

    public void createRoom(Long id, UserDetailsImpl userDetails) {
        Post post = findPost(id);

        String chatTitle = post.getTitle();
        // 게시글 생성 과 함께 채팅방 개설
        ChatRoom chatRoom = ChatRoom.builder()
                .post(post)
                .chatTitle(chatTitle)
                .activated(true)
                .build();

        chatRoomRepository.save(chatRoom);

        // UserChatRooms 생성

        User user = userDetails.getUser();

        UserChatRoom userChatRoom = UserChatRoom.builder().user(user).chatRooms(chatRoom).role("ADMIN").build();

        userChatRoomsRepository.save(userChatRoom);

    }

    // 사용자가 속한 채팅방(모두) 불러오기 (확인 완료)
    // 채팅방 비활성화 (채팅방 저장 테이터중 활성화 필드가 true인 값만 가져온다.)
    // 활성화 필드가 false 인 채팅방의 경우 - 게시글에서 모집완료 된 경우 / 시간을 정한다면 정해진 시간이 다된 경우
    public ChatRoomsResponseDto getRooms(Long id) {
        List<UserChatRoom> userChatRooms = userChatRoomsRepository.findAllByUserId(id);

        List<UserChatRoom> userChatActivatedRooms = new ArrayList<>();
        for (UserChatRoom userChatRoom : userChatRooms) {
            System.out.println(
                    "userChatRoom.getChatRooms().getChatTitle() = " + userChatRoom.getChatRooms().isActivated());
            if (userChatRoom.getChatRooms().isActivated()) {
                userChatActivatedRooms.add(userChatRoom);
            }
        }
        // id / 채팅방 제목
        return new ChatRoomsResponseDto(userChatActivatedRooms);
    }

    // 쿠키에 저장된 사용자 이름으로 사용자 id 가져오기 (확인 완료)
    // 채팅방에 유저 id 엔티티가 없음 (수정 가능성 있음)
    public Long getUser(String id) {
        Optional<User> user = userRepository.findByUsername(id);
        return user.get().getId();
    }

    // 특정 채팅룸 불러오기 (채팅방의 활성화 확인)
    public ChatRoomNameResponseDto getRoom(Long id, UserDetailsImpl userDetails) {

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);

        if (!chatRoom.isPresent()) {
            throw new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.");
        }

        return new ChatRoomNameResponseDto(chatRoom.get());
    }

    // 생성된 채팅 저장 (확인 완료)
    public void createRooms(ChatSaveRequestDto chatSaveRequestDto) {
        System.out.println("chatSaveRequestDto.getCreatedAt() = " + chatSaveRequestDto.getCreatedAt());
        // 채팅을 보낸 유저 조회
        Optional<User> user = userRepository.findByUsername(chatSaveRequestDto.getUserId());
        // 채팅을 저장할 채팅방
        ChatRoom chatRoom = findChatRoom(chatSaveRequestDto.getRoomId());
        Chat chat = Chat.builder()
                .user(user.get())
                .chatRooms(chatRoom)
                .chatContent(chatSaveRequestDto.getChatContent())
                .createdAt(chatSaveRequestDto.getCreatedAt())
                .build();

        chatRepository.save(chat);
    }

    // 특정 채팅방 채팅 조회 (확인 완료)
    public ChatsResponseDto getChat(Long id, UserDetailsImpl userDetails) {
        // 로그인한 유저 id
        Long userId = userDetails.getUser().getId();

        // 채팅방에 속한 유저 테이블 에서 로그인한 유저가 초대된 시간 데이터
        Optional<UserChatRoom> userChatRoom = userChatRoomsRepository.findByChatRoomsIdAndUserId(id, userId);
        LocalDateTime chekTime = userChatRoom.get().getCreatedAt();

        // 해당 유저가 속한 채팅방의 채팅 데이터
        List<Chat> chatList = chatRepository.findAllByChatRoomsId(id);

        // 채팅방에 나타낼 채팅 내용
        List<Chat> showChatList = new ArrayList<>();
        for (Chat chat : chatList) {
            if (chekTime.isBefore(chat.getCreatedAt())) {
                // 초대 된 시간보다 큰것들 (자신의 들어온 후의 채팅)
                System.out.println("chat.getChatContent() = " + chat.getChatContent());
                showChatList.add(chat);
            }
        }
        // 해당 사용자의 방 초대 된 시간 보다 전 채팅은 보이지 않는다.

        return new ChatsResponseDto(showChatList);
    }

    // 게시글에서 신청 버튼 클릭시->작성자 알림-> 작성자 알림 승인-> 사용자 채팅방 추가
    @Transactional
    public void userJoin(Long postId, Long chatRoomId, UserDetailsImpl userDetails) {
        Post post = findPost(postId);
        ChatRoom chatRoom = findChatRoom(chatRoomId);

        //중복참여 방지
        User user = userDetails.getUser();
        if (chatRoom.getUserChatRoomList().size() >= post.getMaxnum()) {
            throw new IllegalArgumentException("모집 인원이 다 찼습니다.");
        } else if (userChatRoomsRepository.existsByUserAndChatRooms(user, chatRoom)) {
            throw new IllegalArgumentException("이미 참여하고 있는 방입니다.");
        }

        //		유저 채팅방 초대
        UserChatRoom guestChatRoom = UserChatRoom.builder()
                .user(user)
                .chatRooms(chatRoom)
                .role("GUEST")
                .build();

        userChatRoomsRepository.save(guestChatRoom);

    }

    @Transactional
    public void joinUserChatRoom(ChatroomJoinRequestDto requestDto, User loginUser) {
        Long postId = requestDto.getPostId();
        Post post = findPost(postId);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByPost_IdAndActivatedIsTrue(postId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 채팅방이 존재하지 않습니다."));

        //중복참여 방지
        Long userId = requestDto.getUserId();
        User joiner = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        if (!Objects.equals(loginUser.getId(), post.getUser().getId())) {
            throw new IllegalArgumentException("게시글 작성자가 아닙니다. 유저를 참가시킬 권한이 없습니다.");
        }
        log.info("현재 정원:" + chatRoom.getUserChatRoomList().size());
        if (chatRoom.getUserChatRoomList().size() >= post.getMaxnum()) {
            throw new IllegalArgumentException("모집 인원이 다 찼습니다.");
        } else if (userChatRoomsRepository.existsByUserAndChatRooms(joiner, chatRoom)) {
            throw new IllegalArgumentException("이미 참여하고 있는 방입니다.");
        }

        UserChatRoom guestChatRoom = UserChatRoom.builder().user(joiner).chatRooms(chatRoom).role("GUEST").build();
        //유저 채팅방 초대
        userChatRoomsRepository.save(guestChatRoom);

        //알림 발송
        NotificationEvent approveMessageEvent = NotificationEvent.builder()
                .title("참여 요청 승인").notificationType(NotificationType.APPROVE_REQUEST)
                .receiver(joiner).publisher(loginUser).content(post.getTitle() + " 게시글에 초대됐습니다.")
                .postId(postId).build();
        eventPublisher.publishEvent(approveMessageEvent);
        //추가 후 인원이 모두 찼는지 확인
        if (chatRoom.getUserChatRoomList().size() + 1 >= post.getMaxnum()) {
            post.setRecruitment(false);
            //기존 chatRoom에 있는 유저 목록
            List<User> users = new ArrayList<>(
                    chatRoom.getUserChatRoomList().stream().map(UserChatRoom::getUser).toList());
            users.add(joiner);
            for (User user : users) {
                NotificationEvent doneMessageEvent = NotificationEvent.builder()
                        .title("모집 완료 알림")
                        .notificationType(NotificationType.RECRUIT_DONE).receiver(user)
                        .publisher(loginUser).content(post.getTitle() + " 게시글 모집이 완료되었습니다.")
                        .postId(postId).build();
                eventPublisher.publishEvent(doneMessageEvent);
            }
        }
    }

    // 사용자 채팅방에서 다른사용자 추방 (ADMIN 용)
    // 특청 사용자의 id를 이용해 테이블에서 유저 삭제
    // 강퇴된 유저가 다시 채팅방에 신청 했을 때 생각하기 (미구현)
    public void kickoutUser(Long userId, Long chatid, UserDetailsImpl userDetails) {
        // 삭제를 요청하는 사람이 해당방의 방장인지 확인
        Long adminUserId = userDetails.getUser().getId();
        Optional<UserChatRoom> adminUserChack = userChatRoomsRepository.findByChatRoomsIdAndUserId(chatid, adminUserId);

        if (!adminUserChack.isPresent()) {
            throw new IllegalArgumentException("채팅방의 방장이 아닙니다.");
        }

        // 강퇴할 사용자의 id를 가지고 조회하는데 추가적으로 해당 채팅방 id 와 같이
        Optional<UserChatRoom> user = userChatRoomsRepository.findByChatRoomsIdAndUserId(chatid, userId);

        userChatRoomsRepository.delete(user.get());
    }

    // 사용자 채팅방 나가기 (USER 용)
    public void outUser(Long id, UserDetailsImpl userDetails) {
        // 해당 사용자 id
        Long userId = userDetails.getUser().getId();

        // 나갈 방 조회
        Optional<UserChatRoom> chatRoom = userChatRoomsRepository.findByChatRoomsIdAndUserId(id, userId);

        if (chatRoom.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        userChatRoomsRepository.delete(chatRoom.get());
    }

    // 채팅방 사용자들 조회
    // 채팅방 id를 사용해서 조회
    public ChatRoomsResponseDto getChatRoomUsers(Long id) {
        // 해당 채팅방이 있는지 확인
        List<UserChatRoom> chatRoom = userChatRoomsRepository.findAllByChatRoomsId(id);

        if (chatRoom.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }
        // 해당 채팅방의 id 를 가진 유저들을 조회

//		List<UserChatRoom> chatRoomUsers = userChatRoomsRepository.findAllByChatRoomsId(id);

        for (UserChatRoom userChatRoom : chatRoom) {
            System.out.println("userChatRoom = " + userChatRoom.getUser().getUsername());
        }

        return new ChatRoomsResponseDto(chatRoom);
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));
    }

    private ChatRoom findChatRoom(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 입니다."));
    }

}

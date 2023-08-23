package com.beteam.willu.stomp;

import com.beteam.willu.post.Post;
import com.beteam.willu.post.PostRepository;
import com.beteam.willu.security.UserDetailsImpl;
import com.beteam.willu.stomp.dto.ChatRoomsResponseDto;
import com.beteam.willu.stomp.dto.ChatSaveRequestDto;
import com.beteam.willu.stomp.dto.ChatsResponseDto;
import com.beteam.willu.stomp.dto.createRoomDto;
import com.beteam.willu.stomp.entity.Chat;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRepository;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.User;
import com.beteam.willu.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomsRepository userChatRoomsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;


    // 게시물이 생성되었을때 채팅룸 생성 (확인 완료)

    public void createRoom(Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);
        createRoomDto roomName = createRoomDto.create();

        String chatTitle = post.get().getTitle();
        // 게시글 생성 과 함께 채팅방 개설
        ChatRoom chatRoom = ChatRoom.builder().post(post.get()).roomName(roomName.getRoomName()).chatTitle(chatTitle).activated(true).build();

        chatRoomRepository.save(chatRoom);

        // UserChatRooms 생성

        User user = userDetails.getUser();

        UserChatRoom userChatRoom = UserChatRoom.builder().user(user).chatRooms(chatRoom).role("ADMIN").build();

        userChatRoomsRepository.save(userChatRoom);

    }

    // 사용자가 속한 채팅방(모두) 불러오기 (확인 완료)
    public ChatRoomsResponseDto getRooms(Long id) {
        List<UserChatRoom> userChatRooms = userChatRoomsRepository.findByUserId(id);

        // id / 채팅방 제목
        return new ChatRoomsResponseDto(userChatRooms);
    }

    // 쿠키에 저장된 사용자 이름으로 사용자 id 가져오기 (확인 완료)
    // 채팅방에 유저 id 엔티티가 없음 (수정 가능성 있음)
    public Long getUser(String id) {
        Optional<User> user = userRepository.findByUsername(id);
        return user.get().getId();
    }

//    // 특정 채팅 방조회
//    public ChatRoomNameResponseDto getRoom(Long id, UserDetailsImpl userDetails) {
//        // 해당 채팅방이 있는지
//        Optional<ChatRoom> chatRoom = chatRoomRepository.findByPostId(id);
//
//        if (!chatRoom.isPresent()) {
//            throw new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.");
//        }
//
//        return new ChatRoomNameResponseDto(chatRoom.get());
//    }


    // 생성된 채팅 저장 (확인 완료)
    public void createRooms(ChatSaveRequestDto chatSaveRequestDto) {
        // 채팅을 보낸 유저 조회
        Optional<User> user = userRepository.findByUsername(chatSaveRequestDto.getUserId());
        // 채팅을 저장할 채팅방
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatSaveRequestDto.getRoomId());

        Chat chat = Chat.builder().user(user.get()).chatRooms(chatRoom.get()).chatContent(chatSaveRequestDto.getChatContent()).build();

        chatRepository.save(chat);
    }

    // 특정 채팅방 채팅 조회 (확인 완료)
    public ChatsResponseDto getChat(Long id) {
        List<Chat> chatList = chatRepository.findAllByChatRoomsId(id);

        return new ChatsResponseDto(chatList);
    }

}

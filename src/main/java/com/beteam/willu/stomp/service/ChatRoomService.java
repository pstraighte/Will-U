package com.beteam.willu.stomp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beteam.willu.blacklist.repository.BlacklistRepository;
import com.beteam.willu.common.security.UserDetailsImpl;
import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.dto.ChatRoomsResponseDto;
import com.beteam.willu.stomp.dto.ChatSaveRequestDto;
import com.beteam.willu.stomp.dto.ChatsResponseDto;
import com.beteam.willu.stomp.entity.Chat;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.entity.UserChatRoom;
import com.beteam.willu.stomp.repository.ChatRepository;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import com.beteam.willu.stomp.repository.UserChatRoomsRepository;
import com.beteam.willu.user.entity.User;
import com.beteam.willu.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final UserChatRoomsRepository userChatRoomsRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ChatRepository chatRepository;
	private final BlacklistRepository blacklistRepository;

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
		ChatRoom chatRoom = findChatRoom(chatSaveRequestDto.getRoomId());
		Chat chat = Chat.builder()
			.user(user.get())
			.chatRooms(chatRoom)
			.chatContent(chatSaveRequestDto.getChatContent())
			.build();

		chatRepository.save(chat);
	}

	// 특정 채팅방 채팅 조회 (확인 완료)
	public ChatsResponseDto getChat(Long id) {
		List<Chat> chatList = chatRepository.findAllByChatRoomsId(id);

		return new ChatsResponseDto(chatList);
	}

	// 게시글에서 신청 버튼 클릭시 사용자 채팅방 추가
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

		if (!chatRoom.isPresent()) {
			throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
		}

		userChatRoomsRepository.delete(chatRoom.get());
	}

	// 채팅방 사용자들 조회
	// 채팅방 id를 사용해서 조회
	public ChatRoomsResponseDto getChatRoomUsers(Long id) {
		// 해당 채팅방이 있는지 확인
		Optional<UserChatRoom> chatRoom = userChatRoomsRepository.findById(id);

		if (!chatRoom.isPresent()) {
			throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
		}
		// 해당 채팅방의 id 를 가진 유저들을 조회

		List<UserChatRoom> chatRoomUsers = userChatRoomsRepository.findAllByChatRoomsId(id);

		for (UserChatRoom userChatRoom : chatRoomUsers) {
			System.out.println("userChatRoom = " + userChatRoom.getUser().getUsername());
		}

		return new ChatRoomsResponseDto(chatRoomUsers);
	}

	private Post findPost(Long id) {
		return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));
	}

	private ChatRoom findChatRoom(Long id) {
		return chatRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방 입니다."));
	}
}

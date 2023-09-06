package com.beteam.willu.common.batch;

import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class testTasklet implements Tasklet {
    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("=====Start execute======");
        // 활성화 되어있는 게시글을 가져온다.
        List<Post> postList = postRepository.findAllByRecruitment(true);

        // 현재 시간 데이터를 생성
        LocalDateTime dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        dateTime = LocalDateTime.parse(startTime, formatter);

        // 활성화 되어있는 채팅방을 가져온다.
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByActivated(true);

        // 시간이 지난 게시글은 비활성화로 변경
        for (Post post : postList) {
            if (post.getPromiseTime().isBefore(dateTime)) {
                // 마감 시간이 지난 게시글
                post.updateRecruitment(false);

                // 마감 시간이 지난 게시글의 id 로 해당 게시글의 채팅방도 비활성화 한다.
                for (ChatRoom chatRoom : chatRoomList) {
                    // 위에서 마감된 게시글의 id와 같은 채팅방을 비활성화 한다.
                    if (chatRoom.getId() == post.getId()) {
                        chatRoom.updateActivated(false);
                    }
                }

            }
        }
        return RepeatStatus.FINISHED;
    }

}
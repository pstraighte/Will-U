package com.beteam.willu.common.batch;

import com.beteam.willu.post.entity.Post;
import com.beteam.willu.post.repository.PostRepository;
import com.beteam.willu.stomp.entity.ChatRoom;
import com.beteam.willu.stomp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class testTasklet implements Tasklet {
    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JobRepository jobRepository;
    private final JobCleanupService jobCleanupService;

    @Override
    //    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("=====Start execute======");

        // 생성된 job 데이터 삭제
        jobCleanupService.cleanupOldJobData();

        // 활성화 되어있는 게시글을 가져온다.
        List<Post> truePostList = postRepository.findAllByRecruitmentOrderByCreatedAtDesc(true);


        // 현재 시간 데이터를 생성
        LocalDateTime dateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        dateTime = LocalDateTime.parse(startTime, formatter);


        // 시간이 지난 게시글은 비활성화로 변경
        for (Post truePost : truePostList) {
            if (truePost.getPromiseTime().isBefore(dateTime)) {
                // 마감 시간이 지난 게시글
                if (truePost.getRecruitment()) { // 게시글의 활성화 필드가 true 인지 확인
                    System.out.println("게시물 false로 변경");
                    truePost.updateRecruitment(false);
                }
            }
        }

        // 비활성화 되어있는 게시글을 가져온다.
        List<Post> falsePostList = postRepository.findAllByRecruitmentOrderByCreatedAtDesc(false);


        // false 로 설정된 게시글의 채팅방이 모집완료 후 1시간이 지났는지 확인
        for (Post falsePost : falsePostList) {
            ChatRoom chatRoom = falsePost.getChatRoom();
            // 비활성화 된 게시글의 모집완료 시간의 1시간 후 데이터 생성
            LocalDateTime falsePostTime = falsePost.getPromiseTime();
            LocalDateTime falsePostTimeAfter = falsePostTime.plus(1, ChronoUnit.HOURS);

            // 1. false 로 설정된 게시글의 id와 같은 채팅방
            // 2. 채팅방 생성 기준으로 1시간이 지난 채팅방 (테스트 2분)
            if (falsePostTimeAfter.isBefore(dateTime)) {
                // 해당 채팅방의 활성화 필드를 false 로 변경
                if (chatRoom.isActivated()) {  // 채팅방의 활성화 필드가 true 인지 확인
                    System.out.println("채팅방 false로 변경");
                    chatRoom.updateActivated(false);
                }
            }
        }
        return RepeatStatus.FINISHED;
    }
}
package com.beteam.willu.blacklist;

import com.beteam.willu.user.User;
import com.beteam.willu.user.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;

    public void addBlacklist(Long id1, Long id2) {  //차단 기능
        User reciver = findUser(id1);
        User sender = findUser(id2);

        Optional<Blacklist> existingBlacklist = blacklistRepository.findByIdAndId(reciver.getId(), sender.getId());
        if (existingBlacklist.isPresent()) {
            throw new DuplicateRequestException("이미 차단한 유저입니다.");
        }
        Blacklist blacklist = new Blacklist(reciver.getId(),sender.getId());
        blacklistRepository.save(blacklist);
    }

    public void removeBlacklist(Long id1, Long id2) {
        Optional<Blacklist> existingBlacklist = blacklistRepository.findByIdAndId(id1, id2);
        if (existingBlacklist.isPresent()) {
            blacklistRepository.delete(existingBlacklist.get());
        } else {
            throw new IllegalArgumentException("해당 유저의 차단이 존재하지 않습니다.");
        }
    }



    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}

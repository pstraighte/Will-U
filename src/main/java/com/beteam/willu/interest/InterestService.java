package com.beteam.willu.interest;

import com.beteam.willu.user.User;
import com.beteam.willu.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;

    public void addInterest(Long id1, Long id2) {   //관심 유저 추가
        User reciver = findUser(id1);
        User sender = findUser(id2);

        if (reciver != null && sender != null) {
            Interest interest = new Interest(reciver.getId(), sender.getId());
            interestRepository.save(interest);
        } else {
            throw new IllegalArgumentException("관심 유저 추가에 실패했습니다.");
        }
    }
    public void removeInterest(Long id1, Long id2) { //관심 유저 해제
        Optional<Interest>existingInterest = interestRepository.findByIdAndId(id1, id2);
        if (existingInterest.isPresent()) {
            interestRepository.delete(existingInterest.get());
        } else {
            throw new IllegalArgumentException("관심 유저가 아닙니다.");
        }
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }


}

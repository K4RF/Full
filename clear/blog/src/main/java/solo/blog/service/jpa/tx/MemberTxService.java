package solo.blog.service.jpa.tx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.tx.Log;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.LogRepository;
import solo.blog.repository.jpa.tx.MemberTxRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberTxService {
    private final MemberTxRepository memberTxRepository;
    private final LogRepository logRepository;

    @Transactional
    public void joinV1(Member member) {
        Log logMessage = new Log(member.getName());

        log.info("== memberRepository 호출 시작 ==");
        memberTxRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        log.info("== logRepository 호출 시작 ==");
        logRepository.save(logMessage);
        log.info("== logRepository 호출 종료 ==");
    }

    @Transactional
    public void joinV2(Member member) {
        Log logMessage = new Log(member.getName());

        log.info("== memberRepository 호출 시작 ==");
        memberTxRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        log.info("== logRepository 호출 시작 ==");
        try{
            logRepository.save(logMessage);
        }catch (RuntimeException e){
            log.info("log 저장에 실패했습니다. logMessage={}", logMessage.getMessage());
            log.info("정상 흐름 변환");
        }
        log.info("== logRepository 호출 종료 ==");
    }
}

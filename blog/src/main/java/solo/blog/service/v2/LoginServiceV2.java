package solo.blog.service.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Member;
import solo.blog.repository.v2.MemberRepositoryV2;

@Service
@RequiredArgsConstructor
public class LoginServiceV2 {
    private final MemberRepositoryV2 memberRepositoryV2;

    /*
        @return null 로그인 실패
     */
    public Member login(String loginId, String password){
        /*
        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);

        Member member = findMemberOptional.get();
        if(member.getPassword().equals(password)){
            return member;
        }
        else{
            return null;
        }
         */

        return memberRepositoryV2.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
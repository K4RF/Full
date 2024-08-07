package solo.blog.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import solo.blog.entity.v1.Member;
import solo.blog.repository.v1.MemoryMemberRepositoryV1;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static solo.blog.priory.Priory.ADMIN;
import static solo.blog.priory.Priory.USUAL;

public class MemoryMemberRepositoryV1Test {
    MemoryMemberRepositoryV1 memberRepository = MemoryMemberRepositoryV1.getInstance();
    @AfterEach
    void afterEach(){
        memberRepository.clearStore();
    }

    @Test
    void save(){
//        given
        Member member = new Member("hello", USUAL);
//        when
        Member savedMember = memberRepository.save(member);
//        then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void findAll(){
//        given
        Member member1 = new Member("member1", USUAL);
        Member member2 = new Member("member2", ADMIN);

        memberRepository.save(member1);
        memberRepository.save(member2);
//        when
        List<Member> result = memberRepository.findAll();

//        then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);

    }
}
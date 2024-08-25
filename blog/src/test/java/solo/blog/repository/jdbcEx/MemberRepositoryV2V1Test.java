package solo.blog.repository.jdbcEx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import solo.blog.entity.database.Member;
import solo.blog.repository.jdbc.ex.MemberRepositoryV0;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV2V1Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
//        save
        Member member = new Member("memberV0", "tester", "이름", "1234");
        repository.save(member);
//        findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        //update: money: 10000 -> 20000
        repository.update(member.getMemberId(), "tester", "변경됨", "1234");
        Member updatedMember = repository.findById(member.getMemberId());
        log.info("updatedMember={}", updatedMember);
        assertThat(updatedMember.getName()).isEqualTo("변경됨");

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}

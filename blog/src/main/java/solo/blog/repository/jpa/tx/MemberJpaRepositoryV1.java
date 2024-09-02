package solo.blog.repository.jpa.tx;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import solo.blog.entity.database.tx.Member;
import solo.blog.entity.database.tx.QMember;


import java.util.List;
import java.util.Optional;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MemberJpaRepositoryV1 implements MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public MemberJpaRepositoryV1(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Member save(Member member) {
        log.info("Saving member: {}", member);
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        log.info("Finding member by ID: {}", id);
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        QMember member = QMember.member;

        List<Member> foundMembers = query
                .selectFrom(member)
                .where(member.loginId.eq(loginId))
                .fetch();

        if (foundMembers.size() == 1) {
            return Optional.of(foundMembers.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Member> findAll() {
        log.info("Finding all members");
        QMember member = QMember.member;

        List<Member> members = query
                .selectFrom(member)
                .fetch();

        log.info("Found {} members", members.size());
        return members;
    }
}

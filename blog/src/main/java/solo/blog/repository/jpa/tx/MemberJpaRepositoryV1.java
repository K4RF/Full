package solo.blog.repository.jpa.tx;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import solo.blog.entity.database.tx.Member;
import solo.blog.entity.database.tx.QMember;


import java.util.List;
import java.util.Optional;


@Repository
public class MemberJpaRepositoryV1 implements MemberJpaRepository{
    private final EntityManager em;
    private final JPAQueryFactory query;

    public MemberJpaRepositoryV1(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }


    @Override
    public Optional<Member> findByLoginId(String loginId) {
        QMember member = QMember.member;  // QueryDSL의 Q 타입 클래스 사용

        Member foundMember = query
                .selectFrom(member)
                .where(member.loginId.eq(loginId))
                .fetchOne();

        return Optional.ofNullable(foundMember);
    }

    @Override
    public List<Member> findAll() {
        QMember member = QMember.member;

        return query
                .selectFrom(member)
                .fetch();
    }
}

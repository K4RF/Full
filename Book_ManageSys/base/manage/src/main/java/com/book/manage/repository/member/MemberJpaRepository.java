package com.book.manage.repository.member;

import com.book.manage.entity.Member;
import com.book.manage.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MemberJpaRepository implements MemberRepository{
    private final EntityManager em;
    private final JPAQueryFactory query;

    public MemberJpaRepository(EntityManager em) {
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
    public Optional<Member> findById(Long memberId) {
        log.info("Finding member by ID: {}", memberId);
        Member member = em.find(Member.class, memberId);
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
        }else{
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

        log.info("Fount {} members", members.size());
        return members;
    }

    @Override
    public boolean existsByName(String nickname) {
        log.info("Checking if name exists: {}", nickname);
        QMember member = QMember.member;

        long count = query
                .selectFrom(member)
                .where(member.nickname.eq(nickname))
                .fetchCount();
        return count > 0;
    }

    @Override
    public Optional<Member> findByName(String nickname) {
        QMember member = QMember.member;

        List<Member> foundMembers = query
                .selectFrom(member)
                .where(member.nickname.eq(nickname))
                .fetch();

        if (foundMembers.size() == 1) {
            return Optional.of(foundMembers.get(0));
        }else{
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Long memberId) {
        log.info("Attempting to delete member with ID: {}", memberId);  // 삭제 시도 로그
        Member member = em.find(Member.class, memberId);
        if (member != null) {
            log.info("Found member with ID: {}. Proceeding with deletion.", memberId);
            em.remove(member);
            log.info("Deleted member with ID: {}", memberId);  // 삭제 완료 로그
        } else {
            log.warn("No member found with ID: {}", memberId);  // 삭제 실패 로그
        }
    }

}

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
public class MemberJpaRepository implements MemberRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Member save(Member member) {
        log.info("Saving member: {}", member);
        if (member.getMemberId() == null) { // 신규 등록
            em.persist(member);
        } else { // 기존 데이터 수정
            em.merge(member);
        }
        return member;
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        log.info("Finding member by ID: {}", memberId);
        return Optional.ofNullable(em.find(Member.class, memberId));
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        log.info("Finding member by loginId: {}", loginId);
        QMember member = QMember.member;

        return Optional.ofNullable(
                query.selectFrom(member)
                        .where(member.loginId.eq(loginId))
                        .fetchOne()
        );
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

    @Override
    public boolean existsByName(String nickname) {
        log.info("Checking if name exists: {}", nickname);
        QMember member = QMember.member;

        return query
                .selectFrom(member)
                .where(member.nickname.eq(nickname))
                .fetchFirst() != null;
    }

    @Override
    public Optional<Member> findByName(String nickname) {
        log.info("Finding member by nickname: {}", nickname);
        QMember member = QMember.member;

        return Optional.ofNullable(
                query.selectFrom(member)
                        .where(member.nickname.eq(nickname))
                        .fetchOne()
        );
    }

    @Override
    public void deleteById(Long memberId) {
        log.info("Attempting to delete member with ID: {}", memberId);
        QMember member = QMember.member;

        long deletedCount = query.delete(member)
                .where(member.memberId.eq(memberId))
                .execute();

        if (deletedCount > 0) {
            log.info("Deleted member with ID: {}", memberId);
        } else {
            log.warn("No member found with ID: {}", memberId);
        }
    }
}
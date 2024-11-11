package com.book.manage.repository.member;

import com.book.manage.entity.Member;
import com.book.manage.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberJpaRepository implements MemberRepository{
    private final EntityManager em;
    private final JPAQueryFactory query;

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
    public void deleteById(Long id) {
        Member member = em.find(Member.class, id);
        if (member != null) {
            em.remove(member);
            log.info("Deleted member: {}", member);

        }
        else{
            log.warn("Member not found with ID: {}", id);
        }
    }
}

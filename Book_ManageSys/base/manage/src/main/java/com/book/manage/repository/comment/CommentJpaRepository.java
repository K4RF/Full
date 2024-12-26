package com.book.manage.repository.comment;

import com.book.manage.entity.Comment;
import com.book.manage.entity.Member;
import com.book.manage.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class CommentJpaRepository implements CommentRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public CommentJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<Comment> findByBookId(Long bookId) {
        QComment qComment = QComment.comment;
        log.info("Executing query to find comments by bookId: {}", bookId);
        List<Comment> results = queryFactory.selectFrom(qComment)
                .where(qComment.book.bookId.eq(bookId))
                .fetch();

        log.info("Query executed successfully, found {} comments for bookId: {}", results.size(), bookId);
        return results;
    }

    @Override
    public void save(Comment comment) {
        if (comment.getCommentId() == null) {
            log.info("Saving new comment: {}", comment);
            em.persist(comment);
            log.info("New comment saved successfully with id: {}", comment.getCommentId());
        }else{
            log.info("Updating existing comment: {}", comment);
            em.merge(comment);
            log.info("Comment updated successfully with id: {}", comment.getCommentId());
        }
    }

    // 댓글 삭제 로직
    @Override
    public void deleteByBookId(Long bookId) {
        QComment qComment = QComment.comment;
        log.info("Executing query to find comments by bookId: {}", bookId);
        long deletedCount = queryFactory.delete(qComment)
                .where(qComment.book.bookId.eq(bookId))
                .execute();
        log.info("Deleted {} comments for bookId: {}", deletedCount, bookId);
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    @Override
    public void delete(Comment comment) {
        em.remove(comment);
    }

    @Override
    public List<Comment> findByWriter(String writer) {
        QComment qComment = QComment.comment;
        log.info("Executing query to find comments by writer: {}", writer);

        List<Comment> results = queryFactory.selectFrom(qComment)
                .where(qComment.writer.eq(writer))
                .fetch();

        log.info("Query executed successfully, found {} comments for writer: {}", results.size(), writer);
        return results;
    }

    @Override
    public boolean existsByBookIdAndWriter(Long bookId, Member loginMember) {
        if(loginMember == null){
            return false;
        }
        QComment qComment = QComment.comment;
        log.info("Checking if comment exists for bookId: {} and writer: {}", bookId, loginMember);

        long count = queryFactory.selectFrom(qComment)
                .where(qComment.book.bookId.eq(bookId)
                        .and(qComment.writer.eq(loginMember.getNickname())))
                .fetchCount();

        log.info("Existence check completed, count: {}", count);
        return count > 0;
    }
}

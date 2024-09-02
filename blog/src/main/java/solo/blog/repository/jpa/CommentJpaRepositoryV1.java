package solo.blog.repository.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import solo.blog.entity.database.Comment;
import solo.blog.entity.database.QComment;

import java.util.List;

@Slf4j
@Repository
public class CommentJpaRepositoryV1 implements CommentJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public CommentJpaRepositoryV1(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        QComment qComment = QComment.comment;

        log.info("Executing query to find comments by postId: {}", postId);

        // postId를 Post 객체의 id로 수정하여 필터링합니다.
        List<Comment> results = queryFactory.selectFrom(qComment)
                .where(qComment.post.id.eq(postId))
                .fetch();

        log.info("Query executed successfully, found {} comments for postId: {}", results.size(), postId);

        return results;
    }

    @Override
    public void save(Comment comment) {
        if (comment.getId() == null) {
            log.info("Saving new comment: {}", comment);
            em.persist(comment);
            log.info("New comment saved successfully with id: {}", comment.getId());
        } else {
            log.info("Updating existing comment: {}", comment);
            em.merge(comment);
            log.info("Comment updated successfully with id: {}", comment.getId());
        }
    }
}

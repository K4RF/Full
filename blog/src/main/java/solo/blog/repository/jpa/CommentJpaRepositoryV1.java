package solo.blog.repository.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import solo.blog.entity.database.Comment;
import solo.blog.entity.database.QComment;

import java.util.List;

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

        return queryFactory.selectFrom(qComment)
                .where(qComment.postId.eq(postId))
                .fetch();
    }

    @Override
    public void save(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
        } else {
            em.merge(comment);
        }
    }
}
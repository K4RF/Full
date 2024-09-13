package solo.blog.repository.jpa.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import solo.blog.entity.database.QTag;
import solo.blog.entity.database.Tag;

import java.util.Optional;

@Repository
public class TagJpaRepository {

    @PersistenceContext
    private EntityManager em;
    private final JPAQueryFactory queryFactory;

    public TagJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            em.persist(tag);
        } else {
            em.merge(tag);
        }
        return tag;
    }

    public Optional<Tag> findByNameAndPostId(String name, Long postId) {
        QTag qTag = QTag.tag;

        Tag foundTag = queryFactory.selectFrom(qTag)
                .where(qTag.name.eq(name)
                        .and(postId == null ? qTag.postId.isNull() : qTag.postId.eq(postId)))
                .fetchOne();

        return Optional.ofNullable(foundTag);
    }

    public Optional<Tag> findByName(String name) {
        QTag tag = QTag.tag;
        Tag foundTag = queryFactory
                .selectFrom(tag)
                .where(tag.name.eq(name)
                        .and(tag.postId.isNull())) // postId가 null인 경우 찾기
                .fetchOne();

        return Optional.ofNullable(foundTag);
    }
}

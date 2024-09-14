package solo.blog.repository.jpa.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import solo.blog.entity.database.QTag;
import solo.blog.entity.database.Tag;

import java.util.List;
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
            // 중복 체크
            Optional<Tag> existingTag = findByNameAndPostId(tag.getName(), tag.getPost().getId());
            if (existingTag.isPresent()) {
                return existingTag.get(); // 이미 존재하는 태그 반환
            }
            em.persist(tag);
        } else {
            em.merge(tag);
        }
        return tag;
    }

    public Optional<Tag> findByNameAndPostId(String name, Long postId) {
        QTag tag = QTag.tag;
        Tag foundTag = queryFactory
                .selectFrom(tag)
                .where(tag.name.eq(name)
                        .and(tag.post.id.eq(postId)))
                .fetchOne();

        return Optional.ofNullable(foundTag);
    }

    public void delete(Tag tag) {
        if (em.contains(tag)) {
            em.remove(tag);
        } else {
            Tag managedTag = em.find(Tag.class, tag.getId());
            if (managedTag != null) {
                em.remove(managedTag);
            }
        }
    }

    public void deleteByPostId(Long postId) {
        QTag tag = QTag.tag;

        List<Tag> tags = queryFactory
                .selectFrom(tag)
                .where(tag.post.id.eq(postId))
                .fetch();

        for (Tag tagEntity : tags) {
            if (em.contains(tagEntity)) {
                em.remove(tagEntity);
            } else {
                Tag managedTag = em.find(Tag.class, tagEntity.getId());
                if (managedTag != null) {
                    em.remove(managedTag);
                }
            }
        }
    }
}

package solo.blog.repository.jpa;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;

import static solo.blog.entity.database.QPost.post;

@Slf4j
@Repository
public class PostJpaRepositoryV3 implements JpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public PostJpaRepositoryV3(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Post save(Post post) {
        em.persist(post);
        return post;
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        Post findPost = findById(postId).orElseThrow();
        findPost.setTitle(updateParam.getTitle());
        findPost.setContent(updateParam.getContent());
        findPost.setLoginId(updateParam.getLoginId());
    }

    @Override
    public Optional<Post> findById(Long id) {
        Post post = em.find(Post.class, id);
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findAll(PostSearchCond cond) {
        String loginId = cond.getLoginId();
        String title = cond.getTitle();

        List<Post> result = query
                .select(post)
                .from(post)
                .where(
                        likeTitle(title),
                        likeLoginId(loginId)
                )
                .fetch();

        log.info("Generated Query Result: {}", result);
        return result;
    }

    private BooleanExpression likeTitle(String title) {
        return StringUtils.hasText(title) ? post.title.like("%" + title + "%") : null;
    }

    private BooleanExpression likeLoginId(String loginId) {
        return StringUtils.hasText(loginId) ? post.loginId.like("%" + loginId + "%") : null;
    }
}

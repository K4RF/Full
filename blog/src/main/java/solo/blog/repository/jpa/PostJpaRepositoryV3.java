package solo.blog.repository.jpa;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.QPost;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;

import static solo.blog.entity.database.QPost.post;

@Slf4j
@Repository
public class PostJpaRepositoryV3 implements PostJPARepository {
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

    //@Override
    /*public List<Post> findAllOld(PostSearchCond cond) {

        String loginId = cond.getLoginId();
        String title = cond.getTitle();

        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(title)) {
            builder.and(post.title.like("%" + title + "%"));
        }
        if (StringUtils.hasText(loginId)) {
            builder.and(post.loginId.like("%" + loginId + "%"));
        }
        List<Post> result = query
                .select(post)
                .from(post)
                .where(builder)
                .fetch();
        return result;
    }

     */

    @Override
    public List<Post> findAll(PostSearchCond cond) {

        String loginId = cond.getLoginId();
        String title = cond.getTitle();

        List<Post> result = query
                .select(post)
                .from(post)
                .where(likeTitle(title), likeLoginId(loginId))
                .fetch();
        return result;
    }

    private BooleanExpression likeTitle(String title) {
        if (StringUtils.hasText(title)) {
            return post.title.like("%" + title + "%");
        }
        return null;
    }

    private BooleanExpression likeLoginId(String loginId) {
        if (StringUtils.hasText(loginId)) {
            return post.title.like("%" + loginId + "%");
        }
        return null;
    }
}

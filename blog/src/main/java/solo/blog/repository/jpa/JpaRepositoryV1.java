package solo.blog.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jdbc.PostJdbcRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JpaRepositoryV1 implements PostJPARepository {
    private final EntityManager em;

    public JpaRepositoryV1(EntityManager em) {
        this.em = em;
    }

    @Override
    public Post save(Post post) {
        em.persist(post);
        return post;
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        Post findPost = em.find(Post.class, postId);
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
        String jpql = "select i from Post i";
        boolean andFlag = false;

        String loginId = cond.getLoginId();
        String title = cond.getTitle();

        if(StringUtils.hasText(title)){
            jpql += " i.title like concat('%', :title, '%')";
            andFlag = true;
        }
        if(StringUtils.hasText(loginId)){
            if(andFlag){
                jpql += " and";
            }
            jpql += " i.login_id like concat ('%', :login_id, '%')";
        }
        log.info("jpql={}", jpql);
        TypedQuery<Post> query = em.createQuery(jpql, Post.class);
        if (StringUtils.hasText(title)) {
            query.setParameter("title", title);
        }
        if (StringUtils.hasText(loginId)) {
            query.setParameter("login_id", loginId);
        }
        return query.getResultList();
    }
}

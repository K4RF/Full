package solo.blog.repository.jpa;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;

import java.util.List;

import static solo.blog.entity.database.QPost.post;

@Repository
public class PostQueryRepository {
    private final JPAQueryFactory query;

    public PostQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public List<Post> findAll(PostSearchCond cond){
        return query.select(post)
                .from(post)
                .where(
                        likeTitle(cond.getTitle()),
                        likeLoginId(cond.getLoginId())
                ).fetch();
    }

    private BooleanExpression likeTitle(String title){
        if(StringUtils.hasText(title)){
            return post.title.like("%" + title + "%");
        }
        return null;
    }

    private BooleanExpression likeLoginId(String loginId){
        if(StringUtils.hasText(loginId)){
            return post.loginId.like("%" + loginId + "%");
        }
        return null;
    }
}

package solo.blog.repository.jpa;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PostJpaRepositoryV2 implements PostJPARepository {
    private final SpringDataJpaRepository repository;

    public PostJpaRepositoryV2(SpringDataJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Post save(Post post) {
        return repository.save(post);
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        Post findPost = repository.findById(postId).orElseThrow();
        findPost.setTitle(updateParam.getTitle());
        findPost.setContent(updateParam.getContent());
        findPost.setLoginId(updateParam.getLoginId());
    }

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Post> findAll(PostSearchCond cond) {
        boolean hasCondition = false;

        String loginId = cond.getLoginId();
        String title = cond.getTitle();
        if(StringUtils.hasText(title) && StringUtils.hasText(loginId)){
            return repository.findPosts("%" + title + "%", loginId);
        }else if (StringUtils.hasText(title)) {
            return repository.findByTitleLike("%" + title + "%");
        }else if (StringUtils.hasText(loginId)){
            return repository.findByLoginIdLike("%" + loginId + "%");
        }else{
            return repository.findAll();
        }
    }
}

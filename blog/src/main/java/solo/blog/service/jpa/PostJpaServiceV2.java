package solo.blog.service.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.JpaRepositoryV2;
import solo.blog.repository.jpa.PostQueryRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PostJpaServiceV2 implements PostJpaService{
    private final JpaRepositoryV2 jpaRepositoryV2;
    private final PostQueryRepository postQueryRepository;

    @Override
    public Post save(Post post, Set<String> tagNames) {
        return jpaRepositoryV2.save(post);
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
        return jpaRepositoryV2.findById(id);
    }

    @Override
    public List<Post> findPosts(PostSearchCond cond) {
        return postQueryRepository.findAll(cond);
    }
}

package solo.blog.service.jpa.post;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.post.JpaRepositoryV2;
import solo.blog.repository.jpa.post.PostQueryRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Primary
public class PostJpaServiceV2 implements PostJpaService {
    private final JpaRepositoryV2 jpaRepositoryV2;
    private final PostQueryRepository postQueryRepository;
    private final TagJpaService tagJpaService;

    @Transactional
    public Post save(Post post, Set<String> tagNames) {
        Set<Tag> tags = tagJpaService.createTags(tagNames);
        post.setTags(tags);
        return jpaRepositoryV2.save(post);
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        // 게시물 업데이트
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

    private Set<Tag> processTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagJpaService.createOrGetTag(tagName);
            tags.add(tag);
        }
        return tags;
    }
}
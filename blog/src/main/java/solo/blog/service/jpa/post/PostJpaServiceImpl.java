package solo.blog.service.jpa.post;

import jakarta.transaction.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.post.JpaRepository;

import java.util.*;
import java.util.stream.Collectors;

public class PostJpaServiceImpl implements PostJpaService {
    private final JpaRepository repository;
    private final TagJpaService tagService;

    public PostJpaServiceImpl(JpaRepository repository, TagJpaService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    @Transactional
    public Post save(Post post, Set<String> tagNames) {
        post.setTitle(post.getTitle());
        post.setContent(post.getContent());
        post.setLoginId(post.getLoginId());

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagService.createOrGetTag(tagName, post.getId()); // post.getId() 전달
            tags.add(tag);
        }

        // Set<Tag>를 List<Tag>로 변환
        List<Tag> tagList = tags.stream().collect(Collectors.toList());
        post.setTags(tagList);

        return repository.save(post);
    }

    @Override
    @Transactional
    public Post update(Long postId, PostUpdateDto updateParam) {
        Post post = repository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        // 게시글 정보 업데이트
        post.setTitle(updateParam.getTitle());
        post.setContent(updateParam.getContent());
        post.setLoginId(updateParam.getLoginId());

        // 태그 정보 업데이트
        List<Tag> tags = updateParam.getTags().stream()
                .map(tag -> tagService.createOrGetTag(tag.getName(), postId)) // postId 전달
                .collect(Collectors.toList());
        post.setTags(tags);

        return repository.save(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Post> findPosts(PostSearchCond cond) {
        return repository.findAll(cond);
    }

    @Override
    public List<Post> findByLoginId(String loginId) {
        return null;
    }

    @Override
    public void delete(Long postId) {
    }
}

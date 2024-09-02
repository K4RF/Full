package solo.blog.service.jpa.post;

import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.post.JpaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PostJpaServiceImpl implements PostJpaService {
    private final JpaRepository repository;
    private final TagJpaService tagService;

    public PostJpaServiceImpl(JpaRepository repository, TagJpaService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    public Post save(Post post, Set<String> tagNames) {
        post.setTitle(post.getTitle());
        post.setContent(post.getContent());
        post.setLoginId(post.getLoginId());

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagService.createOrGetTag(tagName);
            tags.add(tag);
        }
        post.setTags(tags);

        return repository.save(post);
    }

    @Override
    public Post update(Long postId, PostUpdateDto updateParam) {
        repository.update(postId, updateParam);
        return null;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Post> findPosts(PostSearchCond cond) {
        return repository.findAll(cond);
    }
}

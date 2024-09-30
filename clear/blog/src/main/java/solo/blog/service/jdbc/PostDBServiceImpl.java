package solo.blog.service.jdbc;

import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jdbc.PostJdbcRepository;
import solo.blog.service.v2.TagService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PostDBServiceImpl implements PostDBService{
    private final PostJdbcRepository repository;
    private final TagService tagService;

    public PostDBServiceImpl(PostJdbcRepository repository, TagService tagService) {
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
    public void update(Long postId, PostUpdateDto updateParam) {
        repository.update(postId, updateParam);
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

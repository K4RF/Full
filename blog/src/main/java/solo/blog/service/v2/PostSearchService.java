package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.v2.PostRepository;
import solo.blog.repository.v2.PostSearchRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostSearchService {
    private final PostSearchRepository repository;
    private final TagService tagService;

    public PostSearchService(PostSearchRepository repository, TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

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

    public void update(Long postId, PostUpdateDto updateParam) {
        repository.update(postId, updateParam);
    }

    public Optional<Post> findById(Long id) {
        return repository.findById(id);
    }

    public List<Post> findPosts(PostSearchCond cond) {
        return repository.findAll(cond);
    }
}

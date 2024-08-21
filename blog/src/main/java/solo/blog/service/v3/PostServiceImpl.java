package solo.blog.service.v3;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.v3.PostRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final TagService tagService;

    public PostServiceImpl(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    @Override
    public Post save(String title, String content, String loginId, Set<String> tagNames) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setLoginId(loginId);

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagService.createOrGetTag(tagName);
            tags.add(tag);
        }
        post.setTags(tags);

        return postRepository.save(post);
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        postRepository.update(postId, updateParam);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findItems(PostSearchCond cond) {
        return postRepository.findAll(cond);
    }
}

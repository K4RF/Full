package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.PostRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagService tagService;

    public PostService(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    public Post createPost(String title, String content, String loginId, Set<String> tagNames) {
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
}

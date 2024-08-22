package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagSearch {
    private final PostRepository postRepository;
    private final TagService tagService;

    public TagSearch(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    public List<Post> getPostsByTag(String tagName) {
        Tag tag = tagService.createOrGetTag(tagName);
        return postRepository.findAll().stream()
                .filter(post -> post.getTags().contains(tag))
                .collect(Collectors.toList());
    }
}

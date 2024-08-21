package solo.blog.service.v3;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.PostRepositoryV2;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagSearch {
    private final PostRepositoryV2 postRepositoryV2;
    private final TagService tagService;

    public TagSearch(PostRepositoryV2 postRepositoryV2, TagService tagService) {
        this.postRepositoryV2 = postRepositoryV2;
        this.tagService = tagService;
    }

    public List<Post> getPostsByTag(String tagName) {
        Tag tag = tagService.createOrGetTag(tagName);
        return postRepositoryV2.findAll().stream()
                .filter(post -> post.getTags().contains(tag))
                .collect(Collectors.toList());
    }
}

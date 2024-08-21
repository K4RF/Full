package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.PostRepositoryV2;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagSearchV2 {
    private final PostRepositoryV2 postRepositoryV2;
    private final TagServiceV2 tagServiceV2;

    public TagSearchV2(PostRepositoryV2 postRepositoryV2, TagServiceV2 tagServiceV2) {
        this.postRepositoryV2 = postRepositoryV2;
        this.tagServiceV2 = tagServiceV2;
    }

    public List<Post> getPostsByTag(String tagName) {
        Tag tag = tagServiceV2.createOrGetTag(tagName);
        return postRepositoryV2.findAll().stream()
                .filter(post -> post.getTags().contains(tag))
                .collect(Collectors.toList());
    }
}

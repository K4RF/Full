package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.PostRepositoryV2;

import java.util.HashSet;
import java.util.Set;

@Service
public class PostServiceV2 {
    private final PostRepositoryV2 postRepositoryV2;
    private final TagServiceV2 tagServiceV2;

    public PostServiceV2(PostRepositoryV2 postRepositoryV2, TagServiceV2 tagServiceV2) {
        this.postRepositoryV2 = postRepositoryV2;
        this.tagServiceV2 = tagServiceV2;
    }

    public Post createPost(String title, String content, String loginId, Set<String> tagNames) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setLoginId(loginId);

        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagServiceV2.createOrGetTag(tagName);
            tags.add(tag);
        }
        post.setTags(tags);

        return postRepositoryV2.save(post);
    }
}

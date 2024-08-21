package solo.blog.service.v2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import solo.blog.entity.v2.Post;
import solo.blog.repository.v2.PostRepositoryV2;
import solo.blog.repository.v2.TagRepositoryV2;

import java.util.Set;

public class PostServiceV2Test {

    @Test
    public void testCreatePostWithTags() {
        PostRepositoryV2 postRepositoryV2 = new PostRepositoryV2();
        TagRepositoryV2 tagRepositoryV2 = new TagRepositoryV2();
        TagServiceV2 tagServiceV2 = new TagServiceV2(tagRepositoryV2);
        PostServiceV2 postServiceV2 = new PostServiceV2(postRepositoryV2, tagServiceV2);

        Set<String> tagNames = Set.of("Java", "Spring", "Backend");
        Post post = postServiceV2.createPost("Test Post", "Test Content", "user1", tagNames);

        assertNotNull(post);
        assertEquals(3, post.getTags().size()); // 3개의 태그가 연결되었는지 확인
    }
}

package solo.blog.service.v2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import solo.blog.entity.v2.Post;
import solo.blog.repository.v2.PostRepository;
import solo.blog.repository.v2.TagRepository;

import java.util.Set;

public class PostServiceTest {

    @Test
    public void testCreatePostWithTags() {
        PostRepository postRepository = new PostRepository();
        TagRepository tagRepository = new TagRepository();
        TagService tagService = new TagService(tagRepository);
        PostService postService = new PostService(postRepository, tagService);

        Set<String> tagNames = Set.of("Java", "Spring", "Backend");
        Post post = postService.createPost("Test Post", "Test Content", "user1", tagNames);

        assertNotNull(post);
        assertEquals(3, post.getTags().size()); // 3개의 태그가 연결되었는지 확인
    }
}

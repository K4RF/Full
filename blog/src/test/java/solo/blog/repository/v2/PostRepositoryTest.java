package solo.blog.repository.v2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import solo.blog.entity.v2.Post;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PostRepositoryTest {

    PostRepository postRepository = new PostRepository();
    @AfterEach
    void afterEach() {
        postRepository.clearStore();

    }

    @Test
    void save(){
        Post post = new Post("qwer", "test title1", "test content1");
        Post savedPost = postRepository.save(post);
        Post findPost = postRepository.findById(post.getId());
        assertThat(findPost).isEqualTo(savedPost);
    }

    @Test
    void findAll(){
//        given
        Post post1 = new Post("qwer", "test title1", "test content1");
        Post post2 = new Post("asdf", "test title2", "test content2");

        postRepository.save(post1);
        postRepository.save(post2);
//      when
        List<Post> result = postRepository.findAll();

//        then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(post1, post2);
    }

    @Test
    void updatePost() {
//        given
        Post post1 = new Post("qwer", "test title1", "test content1");
        Post savedItem = postRepository.save(post1);
        Long postId = savedItem.getId();
//        when
        Post updateParam = new Post("asdf", "test title2", "test content2");
        postRepository.update(postId, updateParam);

//        then
        Post findPost = postRepository.findById(postId);

        assertThat(findPost.getLoginId()).isEqualTo(updateParam.getLoginId());
        assertThat(findPost.getContent()).isEqualTo(updateParam.getContent());
    }
}
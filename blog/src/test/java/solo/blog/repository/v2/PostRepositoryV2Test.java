package solo.blog.repository.v2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import solo.blog.entity.v2.Post;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PostRepositoryV2Test {

    PostRepositoryV2 postRepositoryV2 = new PostRepositoryV2();
    @AfterEach
    void afterEach() {
        postRepositoryV2.clearStore();

    }

    @Test
    void save(){
        Post post = new Post("qwer", "test title1", "test content1");
        Post savedPost = postRepositoryV2.save(post);
        Post findPost = postRepositoryV2.findById(post.getId());
        assertThat(findPost).isEqualTo(savedPost);
    }

    @Test
    void findAll(){
//        given
        Post post1 = new Post("qwer", "test title1", "test content1");
        Post post2 = new Post("asdf", "test title2", "test content2");

        postRepositoryV2.save(post1);
        postRepositoryV2.save(post2);
//      when
        List<Post> result = postRepositoryV2.findAll();

//        then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(post1, post2);
    }

    @Test
    void updatePost() {
//        given
        Post post1 = new Post("qwer", "test title1", "test content1");
        Post savedItem = postRepositoryV2.save(post1);
        Long postId = savedItem.getId();
//        when
        Post updateParam = new Post("asdf", "test title2", "test content2");
        postRepositoryV2.update(postId, updateParam);

//        then
        Post findPost = postRepositoryV2.findById(postId);

        assertThat(findPost.getLoginId()).isEqualTo(updateParam.getLoginId());
        assertThat(findPost.getContent()).isEqualTo(updateParam.getContent());
    }
}
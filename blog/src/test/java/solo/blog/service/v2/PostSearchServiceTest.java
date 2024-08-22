package solo.blog.service.v2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.v2.PostSearchRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostSearchServiceTest {
    @Autowired
    PostSearchRepository repository;

    @AfterEach
    void afterEach(){
        if(repository instanceof PostSearchRepository){
            ((PostSearchRepository) repository).clearStore();
        }
    }

    @Test
    void save(){
//        given
        Post post = new Post("test1", "title1", "content2");
//       when
        Post savedPost = repository.save(post);
//        then
        Post findPost = repository.findById(post.getId()).get();
        assertThat(findPost).isEqualTo(savedPost);
    }

    @Test
    void updatePost(){
//        given
        Post post = new Post("test1", "title1", "content1");
        Post savedPost = repository.save(post);
        Long postId = savedPost.getId();

//        when
        PostUpdateDto updateParam = new PostUpdateDto("test1","title2", "conetent2");
        repository.update(postId, updateParam);
//        then
        Post findPost = repository.findById(postId).get();
        assertThat(findPost.getTitle()).isEqualTo(updateParam.getTitle());
        assertThat(findPost.getContent()).isEqualTo(updateParam.getContent());
        assertThat(findPost.getLoginId()).isEqualTo(updateParam.getLoginId());
    }

    @Test
    void findPosts(){
        //        given
        Post post1 = new Post("test1", "title1", "content1");
        Post post2 = new Post("test2", "title2", "content2");
        Post post3 = new Post("test3", "title3", "content3");

        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
        test(null, null, post1, post2, post3);
        test("", null, post1, post2, post3);


        test("title1", null, post1, post2);

        test("tle1", null, post1, post2);
        test("title2", null, post3);

        test(null, "test1", post1);
        test("title1", "test1", post1);
    }


    void test(String title, String loginId, Post... posts) {
        List<Post> result = repository.findAll(new PostSearchCond(title, loginId));
        assertThat(result).containsExactly(posts);
    }
}
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

@SpringBootTest
class PostSearchServiceTest {

    @Autowired
    PostSearchRepository repository;

    @AfterEach
    void afterEach() {
        // clearStore를 호출하여 저장소를 비웁니다.
        repository.clearStore();
    }

    @Test
    void save() {
        // given
        Post post = new Post("test1", "title1", "content1");

        // when
        Post savedPost = repository.save(post);

        // then
        Post findPost = repository.findById(savedPost.getId()).get();
        assertThat(findPost).isEqualTo(savedPost);
    }

    @Test
    void updatePost() {
        // given
        Post post = new Post("test1", "title1", "content1");
        Post savedPost = repository.save(post);
        Long postId = savedPost.getId();

        // when
        PostUpdateDto updateParam = new PostUpdateDto("test1", "title2", "content2");
        repository.update(postId, updateParam);

        // then
        Post findPost = repository.findById(postId).get();
        assertThat(findPost.getTitle()).isEqualTo(updateParam.getTitle());
        assertThat(findPost.getContent()).isEqualTo(updateParam.getContent());
    }

    @Test
    void findPosts() {
        // given
        Post post1 = new Post("test1", "title1", "content1");
        Post post2 = new Post("test2", "title2", "content2");
        Post post3 = new Post("test3", "title3", "content3");

        repository.save(post1);
        repository.save(post2);
        repository.save(post3);

        // 실제 검색 테스트
        test(null, null, post1, post2, post3); // 모든 포스트 검색
        test("title1", null, post1); // title1만 검색
        test("title2", null, post2); // title2만 검색
        test(null, "test3", post3); // test3의 포스트만 검색
    }

    void test(String title, String loginId, Post... expectedPosts) {
        List<Post> result = repository.findAll(new PostSearchCond(title, loginId));

        // 결과를 검사할 때 순서와 상관없이 포함되는지 확인
        assertThat(result).containsExactlyInAnyOrder(expectedPosts);
    }
}
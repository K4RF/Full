package solo.blog.repository.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional // 각 테스트가 자동으로 트랜잭션 내에서 실행되며, 테스트 후 롤백됩니다.
class PostJpaRepositoryV3Test {

    @Autowired
    @Qualifier("postJpaRepositoryV3")  // 정확한 빈 이름을 명시
    private PostJpaRepositoryV3 repository;

    @BeforeEach
    void beforeEach() {
        // 추가 초기화가 필요한 경우 여기에 작성
    }

    @AfterEach
    void afterEach() {
        // 리소스 정리가 필요한 경우 여기에 작성
    }

    @Test
    void save() {
        // given
        Post post = new Post("test1", "title1", "content1");

        // when
        Post savedPost = repository.save(post);

        // then
        Post findPost = repository.findById(savedPost.getId()).orElse(null);
        assertThat(findPost).isNotNull();  // 데이터가 제대로 저장되었는지 확인
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

        // 업데이트 방식이 save를 사용하는 방식으로 변경
        Post updatedPost = repository.findById(postId).orElseThrow();
        updatedPost.setTitle(updateParam.getTitle());
        updatedPost.setContent(updateParam.getContent());
        repository.save(updatedPost); // save 메서드를 사용하여 업데이트

        // then
        Post findPost = repository.findById(postId).get();
        assertThat(findPost.getTitle()).isEqualTo(updateParam.getTitle());
        assertThat(findPost.getContent()).isEqualTo(updateParam.getContent());
        log.info("saved: {}", findPost.getLoginId());
    }

    @Test
    void findPosts() {
        // Given
        Post post1 = new Post("test1", "title1", "content1");
        Post post2 = new Post("test2", "title2", "content2");
        Post post3 = new Post("test3", "title3", "content3");

        repository.save(post1);
        repository.save(post2);
        repository.save(post3);

        // Title 검색 테스트
        test("title1", null, post1);
        test("title2", null, post2);
        test("title3", null, post3);

        // loginId 검색 테스트
        test(null, "test1", post1);
        test(null, "test2", post2);
        test(null, "test3", post3);

        // Title과 loginId 동시 검색 테스트
        test("title1", "test1", post1);
        test("title2", "test2", post2);
        test("title3", "test3", post3);
    }

    void test(String title, String loginId, Post... expectedPosts) {
        // PostSearchCond에 title과 loginId를 넣어 검색 조건 생성
        PostSearchCond cond = new PostSearchCond(loginId, title);

        // 검색 결과 확인
        List<Post> result = repository.findAll(cond);

        // 예상되는 포스트와 일치하는지 확인
        assertThat(result).containsExactlyInAnyOrder(expectedPosts);
    }
}

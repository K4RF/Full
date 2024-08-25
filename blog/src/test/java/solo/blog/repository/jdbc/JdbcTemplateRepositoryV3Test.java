package solo.blog.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.v2.PostSearchRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class JdbcTemplateRepositoryV3Test {

    @Autowired
    @Qualifier("postDBRepositoryV3")  // 주입할 빈의 이름을 명시적으로 지정
    private PostDBRepository repository;

    @Autowired
    PlatformTransactionManager transactionManager;
    TransactionStatus status;

    @BeforeEach
    void beforeEach(){
        // 트랜잭션 시작
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void afterEach() {
        transactionManager.rollback(status);
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
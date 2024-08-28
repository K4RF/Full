package solo.blog.repository.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class PostQueryRepositoryTest {

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private PostJpaRepositoryV3 postJpaRepositoryV3;  // 포스트를 저장하기 위한 레포지토리

    @BeforeEach
    void beforeEach() {
        // 초기화 작업이 필요한 경우 여기에 작성
    }

    @AfterEach
    void afterEach() {
        // 리소스 정리가 필요한 경우 여기에 작성
    }

    @Test
    void findPostsByTitle() {
        // Given
        Post post1 = new Post("test1", "Spring Boot", "Content1");
        Post post2 = new Post("test2", "Spring Data JPA", "Content2");
        Post post3 = new Post("test3", "QueryDSL", "Content3");

        postJpaRepositoryV3.save(post1);
        postJpaRepositoryV3.save(post2);
        postJpaRepositoryV3.save(post3);

        // When
        PostSearchCond cond = new PostSearchCond(null, "Spring");
        List<Post> result = postQueryRepository.findAll(cond);

        // Then
        assertThat(result).containsExactlyInAnyOrder(post1, post2);
    }

    @Test
    void findPostsByLoginId() {
        // Given
        Post post1 = new Post("test1", "Title1", "Content1");
        Post post2 = new Post("test2", "Title2", "Content2");
        Post post3 = new Post("test1", "Title3", "Content3");

        postJpaRepositoryV3.save(post1);
        postJpaRepositoryV3.save(post2);
        postJpaRepositoryV3.save(post3);

        // When
        PostSearchCond cond = new PostSearchCond("test1", null);
        List<Post> result = postQueryRepository.findAll(cond);

        // Then
        assertThat(result).containsExactlyInAnyOrder(post1, post3);
    }

    @Test
    void findPostsByTitleAndLoginId() {
        // Given
        Post post1 = new Post("test1", "Spring Boot", "Content1");
        Post post2 = new Post("test1", "Spring Data JPA", "Content2");
        Post post3 = new Post("test2", "QueryDSL", "Content3");

        postJpaRepositoryV3.save(post1);
        postJpaRepositoryV3.save(post2);
        postJpaRepositoryV3.save(post3);

        // When
        PostSearchCond cond = new PostSearchCond("test1", "Spring");
        List<Post> result = postQueryRepository.findAll(cond);

        // Then
        assertThat(result).containsExactlyInAnyOrder(post1, post2);
    }

    @Test
    void findNoPostsWhenConditionNotMet() {
        // Given
        Post post1 = new Post("test1", "Spring Boot", "Content1");
        Post post2 = new Post("test2", "Spring Data JPA", "Content2");

        postJpaRepositoryV3.save(post1);
        postJpaRepositoryV3.save(post2);

        // When
        PostSearchCond cond = new PostSearchCond("test3", "QueryDSL");
        List<Post> result = postQueryRepository.findAll(cond);

        // Then
        assertThat(result).isEmpty();
    }
}

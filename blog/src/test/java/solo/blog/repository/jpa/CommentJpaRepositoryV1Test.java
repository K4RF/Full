package solo.blog.repository.jpa;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import solo.blog.entity.database.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")  // 'test' 프로파일이 설정되어 있어야 한다면 사용
@Transactional  // 테스트 후 롤백을 위해 사용
class CommentJpaRepositoryV1Test {

    @Autowired
    private CommentJpaRepositoryV1 commentJpaRepositoryV1;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 초기 데이터 추가
        Comment comment1 = new Comment(1L, "Author1", "This is the first comment.");
        Comment comment2 = new Comment(1L, "Author2", "This is the second comment.");
        Comment comment3 = new Comment(2L, "Author3", "This is a comment on another post.");

        commentJpaRepositoryV1.save(comment1);
        commentJpaRepositoryV1.save(comment2);
        commentJpaRepositoryV1.save(comment3);
    }

    @Test
    void findByPostId_shouldReturnCommentsForGivenPostId() {
        // Given
        Long postId = 1L;

        // When
        List<Comment> comments = commentJpaRepositoryV1.findByPostId(postId);

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getPostId()).isEqualTo(postId);
        assertThat(comments.get(1).getPostId()).isEqualTo(postId);
    }

    @Test
    void save_shouldPersistNewComment() {
        // Given
        Comment newComment = new Comment(3L, "Author4", "This is a new comment.");

        // When
        commentJpaRepositoryV1.save(newComment);

        // Then
        List<Comment> comments = commentJpaRepositoryV1.findByPostId(3L);
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getAuthor()).isEqualTo("Author4");
    }
}
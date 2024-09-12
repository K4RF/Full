package solo.blog.repository.jpa;

import solo.blog.entity.database.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository {
    List<Comment> findByPostId(Long postId);

    void save(Comment comment);

    // 새로운 삭제 메서드 추가
    void deleteByPostId(Long postId);

    Optional<Comment> findById(Long commentId); // 댓글 ID로 조회
    void delete(Comment comment); // 댓글 삭제
}


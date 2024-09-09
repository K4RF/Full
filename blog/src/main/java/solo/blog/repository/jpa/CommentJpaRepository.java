package solo.blog.repository.jpa;

import solo.blog.entity.database.Comment;

import java.util.ArrayList;
import java.util.List;

public interface CommentJpaRepository {
    List<Comment> findByPostId(Long postId);

    void save(Comment comment);

    // 새로운 삭제 메서드 추가
    void deleteByPostId(Long postId);
}


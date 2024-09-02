package solo.blog.repository.jpa;

import solo.blog.entity.database.Comment;

import java.util.ArrayList;
import java.util.List;

public interface CommentJpaRepository {
    List<Comment> findByPostId(Long postId);

    void save(Comment comment);
}

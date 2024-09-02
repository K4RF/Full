package solo.blog.service.jpa;

import solo.blog.entity.database.Comment;

import java.util.List;

public interface CommentJpaService {
    List<Comment> getCommentsByPostId(Long postId);

    void addComment(Long postId, String author, String comet);
}

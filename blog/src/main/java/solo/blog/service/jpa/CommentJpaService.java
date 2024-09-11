package solo.blog.service.jpa;

import solo.blog.entity.database.Comment;

import java.util.List;

public interface CommentJpaService {
    List<Comment> getCommentsByPostId(Long postId);

    void addComment(Long postId, String author, String comet);
    void deleteByPostId(Long postId);
    // 새로 추가된 메서드
    void updateComment(Long commentId, String newComet, String loginMemberName); // 댓글 수정
    void deleteComment(Long commentId, String loginMemberName); // 댓글 삭제

}

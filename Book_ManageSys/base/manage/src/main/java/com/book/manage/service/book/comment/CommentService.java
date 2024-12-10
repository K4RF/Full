package com.book.manage.service.book.comment;

import com.book.manage.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByBookId(Long bookId);

    void addComment(Long bookId, String writer, String review);

    void deleteByBookId(Long bookId);

    // 댓글 수정
    void updateComment(Long commentId, String newReview, String loginMemberNickname);

    // 댓글 삭제
    void deleteComment(Long commentId, String loginMemberNickname);

    // 사용자로 댓글 조회
    List<Comment> getCommentsByWriter(String writer);
}

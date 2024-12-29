package com.book.manage.service.comment;

import com.book.manage.entity.Comment;
import com.book.manage.entity.Member;

import java.math.BigDecimal;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByBookId(Long bookId);

    void addComment(Long bookId, String writer, String review, BigDecimal rating);

    void deleteByBookId(Long bookId);

    // 댓글 수정
    void updateComment(Long commentId, String newReview, String loginMemberNickname, BigDecimal rating);

    // 댓글 삭제
    void deleteComment(Long commentId, String loginMemberNickname);

    // 사용자로 댓글 조회
    List<Comment> getCommentsByWriter(String writer);
    boolean hasUserCommented(Long bookId, Member loginMember);
}

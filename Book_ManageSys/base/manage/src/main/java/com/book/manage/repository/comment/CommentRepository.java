package com.book.manage.repository.comment;

import com.book.manage.entity.Comment;
import com.book.manage.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByBookId(Long bookId);

    void save(Comment comment);

    void deleteByBookId(Long bookId);   // 도서 삭제시 댓글도 삭제

    Optional<Comment> findById(Long commentId); // 댓글 ID 로 조회

    void delete(Comment comment);   // 댓글 삭제

    List<Comment> findByWriter(String writer);  // 사용자로 댓글 조회

    boolean existsByBookIdAndWriter(Long bookId, Member loginMember);

}

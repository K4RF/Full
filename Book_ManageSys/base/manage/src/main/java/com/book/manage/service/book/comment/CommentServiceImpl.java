package com.book.manage.service.book.comment;

import com.book.manage.entity.Book;
import com.book.manage.entity.Comment;
import com.book.manage.entity.Member;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Override
    public List<Comment> getCommentsByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    public void addComment(Long bookId, String writer, String review) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
        Comment comment = new Comment(book, writer, review);
        commentRepository.save(comment);
    }

    @Override
    public void deleteByBookId(Long bookId) {
        commentRepository.deleteByBookId(bookId);
    }

    @Override
    public void updateComment(Long commentId, String newReview, String loginMemberNickname) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not Found"));
        if (!comment.getWriter().equals(loginMemberNickname)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다");
        }
        comment.setReview(newReview);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, String loginMemberNickname) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not Found"));
        if (!comment.getWriter().equals(loginMemberNickname)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getCommentsByWriter(String writer) {
        return commentRepository.findByWriter(writer);
    }

    public boolean hasUserCommented(Long bookId, Member loginMember) {
        return commentRepository.existsByBookIdAndWriter(bookId, loginMember);
    }
}

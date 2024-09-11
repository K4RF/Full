package solo.blog.service.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Comment;
import solo.blog.entity.database.Post;
import solo.blog.repository.jpa.CommentJpaRepository;
import solo.blog.repository.jpa.post.JpaRepositoryV2;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class CommentJpaServiceImpl implements CommentJpaService {
    private final CommentJpaRepository commentJpaRepository;
    private final JpaRepositoryV2 postRepository;  // Post를 가져오기 위해 JpaRepositoryV2 추가

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentJpaRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void addComment(Long postId, String author, String comet) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        Comment comment = new Comment(post, author, comet);  // post 객체를 전달
        commentJpaRepository.save(comment);
    }

    @Transactional
    public void deleteByPostId(Long postId) {
        commentJpaRepository.deleteByPostId(postId);
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, String newComet, String loginMemberName) {
        Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().equals(loginMemberName)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }
        comment.setComet(newComet);
        commentJpaRepository.save(comment);  // 수정 후 저장
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String loginMemberName) {
        Comment comment = commentJpaRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().equals(loginMemberName)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }
        commentJpaRepository.delete(comment);  // 댓글 삭제
    }
}

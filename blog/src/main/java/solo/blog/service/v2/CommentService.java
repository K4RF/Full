package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Comment;
import solo.blog.repository.v2.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(String author, String content, Long postId) {
        Comment comment = new Comment(null, author, content, null, postId);
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, String name, String content) {
        Optional<Comment> existingComment = commentRepository.findById(id);
        if (existingComment.isPresent()) {
            Comment comment = existingComment.get();
            comment.setName(name);
            comment.setContent(content);
            return commentRepository.save(comment);
        } else {
            throw new IllegalArgumentException("Comment not found");
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}

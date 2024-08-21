package solo.blog.service.v3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Comment;
import solo.blog.repository.v2.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public void addComment(Long postId, String author, String comet) {
        Comment comment = new Comment(postId, author, comet);
        commentRepository.save(comment);
    }
}

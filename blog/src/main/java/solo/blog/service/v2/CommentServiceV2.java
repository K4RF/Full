package solo.blog.service.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Comment;
import solo.blog.repository.v2.CommentRepositoryV2;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {
    private final CommentRepositoryV2 commentRepositoryV2;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepositoryV2.findByPostId(postId);
    }

    public void addComment(Long postId, String author, String comet) {
        Comment comment = new Comment(postId, author, comet);
        commentRepositoryV2.save(comment);
    }
}

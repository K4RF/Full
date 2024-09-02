package solo.blog.service.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solo.blog.entity.database.Comment;
import solo.blog.repository.jpa.CommentJpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentJpaServiceImpl implements CommentJpaService{
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentJpaRepository.findByPostId(postId);
    }

    @Override
    public void addComment(Long postId, String author, String comet) {
        Comment comment = new Comment(postId, author, comet);
        commentJpaRepository.save(comment);
    }
}

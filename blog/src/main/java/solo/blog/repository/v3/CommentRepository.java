package solo.blog.repository.v3;

import org.springframework.stereotype.Repository;
import solo.blog.entity.v2.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CommentRepository {
    private List<Comment> comments = new ArrayList<>();

    public List<Comment> findByPostId(Long postId) {
        return comments.stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    public void save(Comment comment) {
        comments.add(comment);
    }
}

package solo.blog.repository.v2;


import solo.blog.entity.v2.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentRepository {

    private List<Comment> comments = new ArrayList<>();
    private Long nextId = 1L;

    public List<Comment> findAllByPostId(Long postId) {
        return comments.stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    public Optional<Comment> findById(Long id) {
        return comments.stream()
                .filter(comment -> comment.getId().equals(id))
                .findFirst();
    }

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(nextId++);
            comment.setCreatedDate(LocalDateTime.now());
            comments.add(comment);
        } else {
            findById(comment.getId()).ifPresent(existingComment -> {
                existingComment.setAuthor(comment.getAuthor());
                existingComment.setContent(comment.getContent());
                existingComment.setCreatedDate(LocalDateTime.now());
            });
        }
        return comment;
    }

    public void deleteById(Long id) {
        comments.removeIf(comment -> comment.getId().equals(id));
    }
}

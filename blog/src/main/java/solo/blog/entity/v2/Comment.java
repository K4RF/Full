package solo.blog.entity.v2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long postId;
    private String author;
    private String content;

    public Comment(Long postId, String author, String content) {
        this.postId = postId;
        this.author = author;
        this.content = content;
    }

    // Getters and Setters
}

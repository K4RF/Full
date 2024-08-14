package solo.blog.entity.v2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long postId;
    private String author;
    private String comet;

    public Comment(Long postId, String author, String comet) {
        this.postId = postId;
        this.author = author;
        this.comet = comet;
    }

    // Getters and Setters
}

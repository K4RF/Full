package solo.blog.entity.v2;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdDate;
    private Long postId;  // 포스트와의 연관을 ID로 처리

    public Comment() {
    }

    public Comment(Long id, String author, String content, LocalDateTime createdDate, Long postId) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.createdDate = createdDate;
        this.postId = postId;
    }

}
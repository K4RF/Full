package solo.blog.entity.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotEmpty
    private String author;

    @NotEmpty
    private String comet;  // 변수명을 수정하여 의미를 명확하게 함

    public Comment() {
    }

    public Comment(Post post, String author, String comet) {
        this.post = post;
        this.author = author;
        this.comet = comet;
    }
}

package solo.blog.entity.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 자동 생성
    private Long id;

    @NotNull
    private Long postId;  // 게시물 ID

    @NotEmpty
    private String author;

    @NotEmpty
    private String comet;  // comet -> comment 수정

    public Comment() {
    }

    public Comment(Long postId, String author, String comet) {
        this.postId = postId;
        this.author = author;
        this.comet = comet;
    }
}

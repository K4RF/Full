package solo.blog.entity.database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude // 순환 참조 방지를 위해
    private Set<Post> posts = new HashSet<>();

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }
    public Tag(String name, Long postId) {
        this.name = name;
        this.postId = postId;
    }
}

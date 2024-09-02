package solo.blog.entity.database;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", length = 10, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude // 순환 참조 방지를 위해 추가
    private Set<Tag> tags = new HashSet<>();

    public Post() {
    }

    public Post(String loginId, String title, String content) {
        this.loginId = loginId;
        this.title = title;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(loginId, post.loginId) &&
                Objects.equals(title, post.title) &&
                Objects.equals(content, post.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loginId, title, content);
    }

    public String getTagsFormatted() {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }
}

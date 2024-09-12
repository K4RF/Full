package solo.blog.entity.database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", length = 10, nullable = false)
    private String loginId; // 로그인 ID (외부에 보이지 않음)

    @Column(nullable = false)
    private String authorName; // 작성자의 이름

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private int viewCount = 0; // 조회수 필드

    @CreationTimestamp
    @Column(updatable = false)  // 생성 시 자동 저장, 이후 수정되지 않도록 설정
    private LocalDateTime createdAt; // 등록 시간 필드

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @OrderColumn(name = "tag_order")
    private List<Tag> tags = new ArrayList<>();

    public String getTagsFormatted() {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }

    public void setTagsFormatted(String tagsFormatted) {
        this.tags = Arrays.stream(tagsFormatted.split(","))
                .map(String::trim)
                .map(Tag::new)
                .collect(Collectors.toList());
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void incrementViewCount() {
        this.viewCount++;
    }

}


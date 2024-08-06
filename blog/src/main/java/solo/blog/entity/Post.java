package solo.blog.entity;

import java.time.LocalDateTime;

public class Post {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private Long memberId;


    public Post(Long id, String title, String content, LocalDateTime createdDate, Long memberId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.memberId = memberId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content =" + content +
                ", createdDate =" + createdDate +
                ", memberId =" + memberId +
                '}';
    }
}

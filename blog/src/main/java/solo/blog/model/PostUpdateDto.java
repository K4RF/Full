package solo.blog.model;

import lombok.Data;
import solo.blog.entity.database.Tag;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PostUpdateDto {
    private String title;
    private String content;
    private String loginId;

    // 태그 리스트 추가
    private Set<Tag> tags = new HashSet<>();

    public PostUpdateDto() {}

    public PostUpdateDto(String loginId, String title, String content, Set<Tag> tags) {
        this.loginId = loginId;
        this.title = title;
        this.content = content;
        this.tags = tags;
    }

    // 태그 관련 게터, 세터 추가
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // 태그를 포맷팅하는 메서드 추가
    public String getTagsFormatted() {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }
}

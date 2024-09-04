package solo.blog.model;

import lombok.Data;
import solo.blog.entity.database.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostUpdateDto {
    private Long id; // id 필드 추가
    private String title;
    private String content;
    private String loginId;

    // 태그 리스트를 List로 변경
    private List<Tag> tags = new ArrayList<>();

    public PostUpdateDto() {}

    public PostUpdateDto(Long id, String loginId, String title, String content, List<Tag> tags) {
        this.id = id; // id 필드 초기화
        this.loginId = loginId;
        this.title = title;
        this.content = content;
        this.tags = tags;
    }

    public String getTagsFormatted() {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }
}

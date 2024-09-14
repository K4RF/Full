package solo.blog.model;

import lombok.Data;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostUpdateDto {
    private Long id;
    private String title;
    private String content;
    private String loginId;
    private List<Tag> tags = new ArrayList<>();
    private String authorName; // 작성자의 이름


    // 생성자 정의
    public PostUpdateDto(Long id, String title, String content, String loginId, List<Tag> tags, String authorName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.loginId = loginId;
        this.tags = tags;
        this.authorName = authorName;
    }

    public String getTagsFormatted() {
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }

    public void setTagsFormatted(String tagsFormatted, Post post) {
        this.tags = Arrays.stream(tagsFormatted.split(","))
                .map(String::trim)
                .map(tagName -> new Tag(tagName, post)) // Post 객체와 함께 태그 생성
                .collect(Collectors.toList());
    }

}


package solo.blog.model;

import lombok.Data;
import solo.blog.entity.v2.Tag;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PostUpdateDto {
    private String title;
    private String content;


    public PostUpdateDto(){

    }

    public PostUpdateDto(String title, String content){
        this.title = title;
        this.content = content;
    }

    // 태그 리스트 추가
    private Set<Tag> tags = new HashSet<>();

    // 기존 필드, 생성자, 게터, 세터 생략

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

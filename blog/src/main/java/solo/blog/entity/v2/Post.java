package solo.blog.entity.v2;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class Post {
    private Long id;
    private String loginId;
    private String title;
    private String content;



    public Post(){

    }

    public Post(String loginId,String title, String content){
        this.loginId = loginId;
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

package solo.blog.model;

import lombok.Getter;
import lombok.Setter;
import solo.blog.entity.v2.Tag;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class PostSearchCond {
    private String title;
    private String authorName; // 작성자의 이름


    public PostSearchCond(){

    }

    public PostSearchCond(String authorName, String title) {
        this.authorName = authorName;
        this.title = title;
    }
}

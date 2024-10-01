package solo.blog.model;

import lombok.Getter;
import lombok.Setter;

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

package solo.blog.entity.v2;

import lombok.Getter;
import lombok.Setter;

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
}

package solo.blog.service.v3;

import lombok.Data;

@Data
public class PostSearchCond {

    private String title;
    private String loginId;

    public PostSearchCond() {
    }

    public PostSearchCond(String title, String loginId) {
        this.title = title;
        this.loginId = loginId;
    }
}
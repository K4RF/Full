package solo.blog.service.v1;

import solo.blog.entity.v1.Post;

public interface PostService {
    Post writePost(Long id, String title, String content, Long memberId);
}

package solo.blog.service.v1;

import solo.blog.entity.v1.PostV1;

public interface PostServiceV1 {
    PostV1 writePost(Long id, String title, String content, Long memberId);
}

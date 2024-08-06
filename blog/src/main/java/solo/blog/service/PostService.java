package solo.blog.service;

import solo.blog.entity.Member;
import solo.blog.entity.Post;

import java.time.LocalDateTime;

public interface PostService {
    Post writePost(Long id, String title, String content, LocalDateTime createdDate, Long memberId);
}

package solo.blog.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import solo.blog.priory.Priory;
import solo.blog.service.MemberService;
import solo.blog.service.MemberServiceImpl;
import solo.blog.service.PostService;
import solo.blog.service.PostServiceImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class PostTest {
    MemberService memberService = new MemberServiceImpl();
    PostService postService = new PostServiceImpl();

    @Test
    void writePost(){
        Long id = 1L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Priory.USUAL);
        memberService.join(member);

        Post post = postService.writePost(id, title, content, memberId);
        assertThat(post.getId()).isEqualTo(1L);
    }
}
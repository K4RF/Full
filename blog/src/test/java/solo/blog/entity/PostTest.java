package solo.blog.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solo.blog.config.AppConfig;
import solo.blog.entity.v1.Member;
import solo.blog.priory.Priory;
import solo.blog.service.v1.MemberService;
import solo.blog.service.v1.PostService;

import static org.assertj.core.api.Assertions.*;

public class PostTest {
    MemberService memberService;
    PostService postService;

    @BeforeEach
    public void beforeEach(){
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        postService = appConfig.postService();
    }
    @Test
    void writePost(){
        Long id = 1L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long memberId = 1L;
        Member member = new Member("memberA", Priory.USUAL);
        memberService.join(member);

        Post post = postService.writePost(id, title, content, memberId);
        assertThat(post.getId()).isEqualTo(1L);
    }
}
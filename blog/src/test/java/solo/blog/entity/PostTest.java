package solo.blog.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solo.blog.config.AppConfig;
import solo.blog.entity.v1.Member;
import solo.blog.entity.priory.Priory;
import solo.blog.entity.v1.PostV1;
import solo.blog.service.v1.MemberServiceV1;
import solo.blog.service.v1.PostService;

import static org.assertj.core.api.Assertions.*;

public class PostTest {
    MemberServiceV1 memberServiceV1;
    PostService postService;

    @BeforeEach
    public void beforeEach(){
        AppConfig appConfig = new AppConfig();
        memberServiceV1 = appConfig.memberService();
        postService = appConfig.postService();
    }
    @Test
    void writePost(){
        Long id = 1L;
        String title = "테스트 제목";
        String content = "테스트 내용";
        Long memberId = 1L;
        Member member = new Member("memberA", Priory.USUAL);
        memberServiceV1.join(member);

        PostV1 post = postService.writePost(id, title, content, memberId);
        assertThat(post.getId()).isEqualTo(1L);
    }
}
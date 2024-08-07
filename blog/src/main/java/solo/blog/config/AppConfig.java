package solo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.v1.MemoryMemberRepository;
import solo.blog.service.v1.MemberService;
import solo.blog.service.v1.MemberServiceImpl;
import solo.blog.service.v1.PostService;
import solo.blog.service.v1.PostServiceImpl;

@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemoryMemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }

    @Bean
    public PostService postService(){
        return new PostServiceImpl(memberRepository());
    }

}

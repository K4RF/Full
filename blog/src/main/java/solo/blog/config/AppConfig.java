package solo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.MemberRepository;
import solo.blog.repository.MemoryMemberRepository;
import solo.blog.service.MemberService;
import solo.blog.service.MemberServiceImpl;
import solo.blog.service.PostService;
import solo.blog.service.PostServiceImpl;

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

package solo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.v1.MemoryMemberRepositoryV1;
import solo.blog.service.v1.MemberServiceV1;
import solo.blog.service.v1.MemberServiceV1Impl;
import solo.blog.service.v1.PostService;
import solo.blog.service.v1.PostServiceImpl;

@Configuration
public class AppConfig {
    @Bean
    public MemberServiceV1 memberService(){
        return new MemberServiceV1Impl(memberRepository());
    }

    @Bean(name = "memoryMemberRepository")
    public MemoryMemberRepositoryV1 memberRepository(){
        return new MemoryMemberRepositoryV1();
    }

    @Bean
    public PostService postService(){
        return new PostServiceImpl(memberRepository());
    }

}

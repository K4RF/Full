package solo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.v1.MemoryMemberRepositoryV1;
import solo.blog.repository.v2.MemberRepository;
import solo.blog.service.v1.MemberServiceV1;
import solo.blog.service.v1.MemberServiceV1Impl;
import solo.blog.service.v1.PostServiceV1;
import solo.blog.service.v1.PostServiceV1Impl;
import solo.blog.service.v2.MemberService;

@Configuration
public class AppConfig {
    @Bean
    public MemberService memberService(MemberRepository memberRepository) {
        return new MemberService(memberRepository);
    }
   // @Bean
    public MemberServiceV1 memberService(){
        return new MemberServiceV1Impl(memberRepository());
    }

    @Bean(name = "memoryMemberRepository")
    public MemoryMemberRepositoryV1 memberRepository(){
        return new MemoryMemberRepositoryV1();
    }

    @Bean
    public PostServiceV1 postServiceV1(){
        return new PostServiceV1Impl(memberRepository());
    }

}

package solo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.v1.MemoryMemberRepositoryV1;
import solo.blog.repository.v2.MemberRepositoryV2;
import solo.blog.service.v1.MemberServiceV1;
import solo.blog.service.v1.MemberServiceV1Impl;
import solo.blog.service.v1.PostServiceV1;
import solo.blog.service.v1.PostServiceV1Impl;
import solo.blog.service.v2.MemberServiceV2;

@Configuration
public class AppConfig {
    @Bean
    public MemberServiceV2 memberService(MemberRepositoryV2 memberRepositoryV2) {
        return new MemberServiceV2(memberRepositoryV2);
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

    /* 데이터 소스와 트랜잭션 매니저 직접 등록
    @Bean
    DataSource dataSource() {
	    return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }
    @Bean
    PlatformTransactionManager transactionManager() {
	    return new DataSourceTransactionManager(dataSource());
    }
     */
}

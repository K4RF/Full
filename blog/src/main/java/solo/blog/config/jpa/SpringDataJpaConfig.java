package solo.blog.config.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.post.JpaRepository;
import solo.blog.repository.jpa.post.PostJpaRepositoryV2;
import solo.blog.repository.jpa.post.SpringDataJpaRepository;
import solo.blog.service.jpa.post.PostJpaService;
import solo.blog.service.jpa.post.PostJpaServiceImpl;
import solo.blog.service.jpa.post.TagJpaService;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {
    private final SpringDataJpaRepository springDataJpaRepository;
    private final TagJpaService tagJpaService;

    @Bean(name = "jpaRepositoryV2Data")
    public JpaRepository jpaRepositoryV2Data(){
        return new PostJpaRepositoryV2(springDataJpaRepository);
    }

    @Bean(name = "postJpaServiceV2Spring")
    public PostJpaService postJpaServiceV2(){
        return new PostJpaServiceImpl(jpaRepositoryV2Data(), tagJpaService);
    }
}

package solo.blog.config.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.PostJPARepository;
import solo.blog.repository.jpa.PostJpaRepositoryV2;
import solo.blog.repository.jpa.SpringDataJpaRepository;
import solo.blog.service.jpa.PostJpaService;
import solo.blog.service.jpa.PostJpaServiceImpl;
import solo.blog.service.jpa.TagJpaService;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {
    private final SpringDataJpaRepository springDataJpaRepository;
    private final TagJpaService tagJpaService;

    @Bean
    public PostJPARepository jpaRepositoryV2(){
        return new PostJpaRepositoryV2(springDataJpaRepository);
    }

    @Bean
    public PostJpaService postJpaServiceV2(){
        return new PostJpaServiceImpl(jpaRepositoryV2(), tagJpaService);
    }
}

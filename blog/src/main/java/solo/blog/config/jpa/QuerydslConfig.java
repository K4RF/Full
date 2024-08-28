package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.PostJPARepository;
import solo.blog.repository.jpa.PostJpaRepositoryV3;
import solo.blog.service.jpa.PostJpaService;
import solo.blog.service.jpa.PostJpaServiceImpl;
import solo.blog.service.jpa.TagJpaService;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {
    private final EntityManager em;
    private final TagJpaService tagJpaService;

    @Bean
    PostJPARepository postJPARepositoryQuery(){
        return new PostJpaRepositoryV3(em);
    }

    @Bean
    PostJpaService postJpaServiceQuery(){
        return new PostJpaServiceImpl(postJPARepositoryQuery(), tagJpaService);
    }
}

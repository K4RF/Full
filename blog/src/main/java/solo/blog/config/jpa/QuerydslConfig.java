package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.post.JpaRepository;
import solo.blog.repository.jpa.post.PostJpaRepositoryV3;
import solo.blog.service.jpa.post.PostJpaService;
import solo.blog.service.jpa.post.PostJpaServiceImpl;
import solo.blog.service.jpa.post.TagJpaService;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {
    private final EntityManager em;
    private final TagJpaService tagJpaService;

    @Bean
    JpaRepository postJPARepositoryQuery(){
        return new PostJpaRepositoryV3(em);
    }

    @Bean
    PostJpaService postJpaServiceQuery(){
        return new PostJpaServiceImpl(postJPARepositoryQuery(), tagJpaService);
    }
}

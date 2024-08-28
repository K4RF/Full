package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.*;
import solo.blog.service.jpa.PostJpaService;
import solo.blog.service.jpa.PostJpaServiceV2;
import solo.blog.service.jpa.TagJpaService;

@Configuration
@RequiredArgsConstructor
public class V3Config {
    private final EntityManager em;
    private final JpaRepositoryV2 jpaRepositoryV3;  // Spring Data JPA
    private final TagJpaService tagJpaService;

    @Bean
    public PostQueryRepository postQueryRepository(){
        return new PostQueryRepository(em);
    }

    @Bean(name = "postJPARepositoryV3")
    public PostJPARepository postJPARepositoryV3(){
        return new PostJpaRepositoryV3(em);
    }

    @Bean(name = "postJpaServiceV3")
    public PostJpaService postJpaServiceV3(){
        return new PostJpaServiceV2(jpaRepositoryV3, postQueryRepository());
    }
}

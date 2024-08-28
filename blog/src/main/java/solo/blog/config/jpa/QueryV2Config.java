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
public class QueryV2Config {
    private final EntityManager em;
    private final JpaRepositoryV2 jpaRepositoryExt;  // Spring Data JPA
    private final TagJpaService tagJpaService;

    @Bean
    public PostQueryRepository postQueryRepository(){
        return new PostQueryRepository(em);
    }

    @Bean(name = "postJpaRepositoryQueryExt")
    public PostJPARepository postJpaRepositoryQueryExt() {
        return new PostJpaRepositoryV3(em);
    }

    @Bean(name = "postJpaServiceV3")
    public PostJpaService postJpaServiceV3(){
        return new PostJpaServiceV2(jpaRepositoryExt, postQueryRepository());
    }
}

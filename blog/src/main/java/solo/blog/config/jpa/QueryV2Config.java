package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.post.JpaRepository;
import solo.blog.repository.jpa.post.JpaRepositoryV2;
import solo.blog.repository.jpa.post.PostJpaRepositoryV3;
import solo.blog.repository.jpa.post.PostQueryRepository;
import solo.blog.service.jpa.post.PostJpaService;
import solo.blog.service.jpa.post.PostJpaServiceV2;
import solo.blog.service.jpa.post.TagJpaService;

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
    public JpaRepository postJpaRepositoryQueryExt() {
        return new PostJpaRepositoryV3(em);
    }

    @Bean(name = "postJpaServiceV3Ext")
    public PostJpaService postJpaServiceV3Ext(){
        return new PostJpaServiceV2(jpaRepositoryExt, postQueryRepository(), tagJpaService);
    }
}

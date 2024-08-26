package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.PostJpaRepositoryV1;
import solo.blog.service.jpa.PostJpaService;
import solo.blog.service.jpa.PostJpaServiceImpl;
import solo.blog.service.jpa.TagJpaService;

@Configuration
public class JpaConfigV1 {
    private final EntityManager em;
    private final TagJpaService tagJpaService;

    public JpaConfigV1(EntityManager em, TagJpaService tagJpaService) {
        this.em = em;
        this.tagJpaService = tagJpaService;
    }

    @Bean
    public PostJpaService postJpaService(){
        return new PostJpaServiceImpl(postJpaRepositoryV1(), tagJpaService);
    }

    @Bean
    public PostJpaRepositoryV1 postJpaRepositoryV1(){
        return new PostJpaRepositoryV1(em);
    }
}

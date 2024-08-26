package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.JpaRepositoryV1;
import solo.blog.repository.jpa.PostJPARepository;
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
        return new PostJpaServiceImpl(repository(), tagJpaService);
    }

    @Bean
    public PostJPARepository repository(){
        return new JpaRepositoryV1(em);
    }
}

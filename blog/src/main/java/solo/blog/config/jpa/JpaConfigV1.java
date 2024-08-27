package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import solo.blog.repository.jpa.PostJPARepository;
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
    @Primary
    public PostJPARepository postJpaRepositoryV1Another(EntityManager em) {
        return new PostJpaRepositoryV1(em);  // 메서드 이름 중복 해결
    }

    @Bean
    public PostJpaService postJpaServiceV1(){
        return new PostJpaServiceImpl(postJpaRepositoryV1(), tagJpaService);
    }

    @Bean
    public PostJpaRepositoryV1 postJpaRepositoryV1(){
        return new PostJpaRepositoryV1(em);
    }
}

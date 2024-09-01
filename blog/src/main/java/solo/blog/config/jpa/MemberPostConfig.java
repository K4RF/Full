package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.post.JpaRepository;
import solo.blog.repository.jpa.post.JpaRepositoryV2;
import solo.blog.repository.jpa.post.PostJpaRepositoryV3;
import solo.blog.repository.jpa.post.PostQueryRepository;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.repository.jpa.tx.MemberJpaRepositoryV1;
import solo.blog.service.jpa.post.PostJpaService;
import solo.blog.service.jpa.post.PostJpaServiceV2;
import solo.blog.service.jpa.post.TagJpaService;
import solo.blog.service.jpa.tx.MemberJpaService;
import solo.blog.service.jpa.tx.MemberJpaServiceImpl;

@Configuration
@RequiredArgsConstructor
public class MemberPostConfig {
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

    @Bean
    public MemberJpaRepository memberJpaRepository(){
        return new MemberJpaRepositoryV1(em);
    }

    @Bean
    public MemberJpaService memberJpaService() {
        return new MemberJpaServiceImpl(memberJpaRepository());
    }
}

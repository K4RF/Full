package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import solo.blog.repository.jpa.CommentJpaRepository;
import solo.blog.repository.jpa.CommentJpaRepositoryV1;
import solo.blog.repository.jpa.post.*;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.repository.jpa.tx.MemberJpaRepositoryV1;
import solo.blog.service.jpa.CommentJpaService;
import solo.blog.service.jpa.CommentJpaServiceImpl;
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
    private final PasswordEncoder passwordEncoder;

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
        return new PostJpaServiceV2(jpaRepositoryExt, postQueryRepository(), new TagJpaRepository(em), commentJpaService());
    }

    @Bean
    public MemberJpaRepository memberJpaRepository(){
        return new MemberJpaRepositoryV1(em);
    }

    @Bean(name = "memberJpaService")
    public MemberJpaService memberJpaService() {
        return new MemberJpaServiceImpl(memberJpaRepository(), postJpaServiceV3Ext(), passwordEncoder);
    }

    @Bean
    public CommentJpaRepository commentJpaRepository(){
        return new CommentJpaRepositoryV1(em);
    }

    @Bean
    public CommentJpaService commentJpaService() {
        return new CommentJpaServiceImpl(commentJpaRepository(), jpaRepositoryExt);
    }
}

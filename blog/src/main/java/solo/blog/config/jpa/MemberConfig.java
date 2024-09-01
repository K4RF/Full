package solo.blog.config.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.repository.jpa.tx.MemberJpaRepositoryV1;
import solo.blog.service.jpa.tx.MemberJpaService;
import solo.blog.service.jpa.tx.MemberJpaServiceImpl;

@Configuration
@RequiredArgsConstructor
public class MemberConfig {
    private final EntityManager em;

    @Bean
    public MemberJpaRepository memberJpaRepository(){
        return new MemberJpaRepositoryV1(em);
    }

    @Bean
    public MemberJpaService memberJpaService(){
        return new MemberJpaServiceImpl(memberJpaRepository());
    }
}

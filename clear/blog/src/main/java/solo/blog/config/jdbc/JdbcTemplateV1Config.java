package solo.blog.config.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jdbc.JdbcTemplateRepositoryV1;
import solo.blog.repository.jdbc.PostJdbcRepository;
import solo.blog.service.jdbc.PostDBService;
import solo.blog.service.jdbc.PostDBServiceImpl;
import solo.blog.service.v2.TagService;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV1Config {
    private final DataSource dataSource;
    private final TagService tagService;

    @Bean
    public PostJdbcRepository postDBRepository(){
        return new JdbcTemplateRepositoryV1(dataSource);
    }

    @Bean
    public PostDBService postDBService(){
        return new PostDBServiceImpl(postDBRepository(), tagService);
    }
}

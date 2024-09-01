package solo.blog.config.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jdbc.JdbcTemplateRepositoryV3;
import solo.blog.repository.jdbc.PostJdbcRepository;
import solo.blog.service.jdbc.PostDBService;
import solo.blog.service.jdbc.PostDBServiceImpl;
import solo.blog.service.v2.TagService;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV3Config {
    private final DataSource dataSource;
    private final TagService tagService;

    @Bean(name = "postDBRepositoryV3")
    public PostJdbcRepository postDBRepository() {
        return new JdbcTemplateRepositoryV3(dataSource);
    }

    @Bean(name = "postDBServiceV3")
    public PostDBService postDBService() {
        return new PostDBServiceImpl(postDBRepository(), tagService);
    }
}

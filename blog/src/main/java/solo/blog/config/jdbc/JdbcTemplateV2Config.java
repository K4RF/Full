package solo.blog.config.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import solo.blog.repository.jdbc.JdbcTemplateRepositoryV2;
import solo.blog.repository.jdbc.PostJdbcRepository;
import solo.blog.service.jdbc.PostDBService;
import solo.blog.service.jdbc.PostDBServiceImpl;
import solo.blog.service.v2.TagService;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV2Config {
    private final DataSource dataSource;
    private final TagService tagService;

    @Bean(name = "postDBRepositoryV2")
    public PostJdbcRepository postDBRepository() {
        return new JdbcTemplateRepositoryV2(dataSource);
    }

    @Bean(name = "postDBServiceV2")
    public PostDBService postDBService() {
        return new PostDBServiceImpl(postDBRepository(), tagService);
    }
}

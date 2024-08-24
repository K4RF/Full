package solo.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import solo.blog.config.jdbc.JdbcTemplateV1Config;
import solo.blog.config.jdbc.JdbcTemplateV2Config;
import solo.blog.config.jdbc.JdbcTemplateV3Config;
import solo.blog.repository.jdbc.PostDBRepository;

@Import(JdbcTemplateV3Config.class)
@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}
}

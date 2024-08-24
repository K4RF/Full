package solo.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import solo.blog.config.jdbc.JdbcTemplateV1Config;
import solo.blog.config.jdbc.JdbcTemplateV2Config;

@Import(JdbcTemplateV2Config.class)
@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

}

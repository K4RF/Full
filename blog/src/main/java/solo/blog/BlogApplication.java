package solo.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import solo.blog.config.jdbc.JdbcTemplateV1Config;
import solo.blog.config.jdbc.JdbcTemplateV2Config;
import solo.blog.config.jdbc.JdbcTemplateV3Config;
import solo.blog.repository.jdbc.PostDBRepository;

import javax.sql.DataSource;

@Slf4j
@Import(JdbcTemplateV3Config.class)
@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	/*@Bean
	@Profile("test")
	public DataSource dataSource() {
		log.info("메모리 데이터베이스 초기화");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	 */
}

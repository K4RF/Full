package com.book.manage;

import com.book.manage.config.TotalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Import(TotalConfig.class)
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.book.manage.repository")
@EntityScan(basePackages = "com.book.manage.entity")
public class ManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageApplication.class, args);
	}

}

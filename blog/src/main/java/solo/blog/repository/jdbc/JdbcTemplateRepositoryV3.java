package solo.blog.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SimpleJdbcInsert
 */
@Slf4j
@Repository
public class JdbcTemplateRepositoryV3 implements PostJdbcRepository {
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateRepositoryV3(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("post")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Post save(Post post) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(post);
        Number key = jdbcInsert.executeAndReturnKey(param);
        post.setId(key.longValue());
        return post;
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        String sql = "update post " +
                "set title=:title, content=:content, login_id=:login_id " + "where id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("title", updateParam.getTitle())
                .addValue("content", updateParam.getContent())
                .addValue("login_id", updateParam.getLoginId())
                .addValue("id", postId);
        template.update(sql, param);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "select id, title, content, login_id from post where id= :id";
        try {
            Map<String, Object> param = Map.of("id", id);
            Post post = template.queryForObject(sql, param, postRowMapper());
            return Optional.of(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findAll(PostSearchCond cond) {
        String postName = cond.getTitle();
        String loginName = cond.getAuthorName();
        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        // 수정된 테이블 이름: post
        String sql = "select id, title, content, login_id from post";

        // 조건이 있는 경우 WHERE 절을 추가
        if (StringUtils.hasText(postName) || StringUtils.hasText(loginName)) {
            sql += " where";
        }

        boolean andFlag = false;

        // title로 검색하는 조건 추가
        if (StringUtils.hasText(postName)) {
            sql += " title like concat('%', :title, '%')";
            andFlag = true;
        }

        // login_id로 검색하는 조건 추가
        if (StringUtils.hasText(loginName)) {
            if (andFlag) {
                sql += " and";
            }
            sql += " login_id like concat('%', :loginId, '%')";
        }

        // SQL 쿼리 로깅
        log.info("Generated SQL: {}", sql);

        // 쿼리 실행
        return template.query(sql, param, postRowMapper());
    }


    private RowMapper<Post> postRowMapper() {
        return BeanPropertyRowMapper.newInstance(Post.class); //camel 변환 지원
    }
}

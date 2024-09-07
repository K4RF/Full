package solo.blog.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
 * NamedParameterJdbcTemplate
 * SqlParameterSource
 * - BeanPropertySqlParameterSource
 * - MapSqlParameterSource
 * Map
 *
 * BeanPropertyRowMapper
 *
 */
@Slf4j
@Repository
public class JdbcTemplateRepositoryV2 implements PostJdbcRepository {
    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Post save(Post post) {
        String sql = "insert into post (title, content, login_id) " + "values (:title, :content, :login_id)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(post);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        Long key = keyHolder.getKey().longValue();
        post.setId(key);
        return post;
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        String sql = "update post " + "set title=:title, content=:content, login_id=:login_id where id=:id";
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
        String sql = "select id, title, content, login_id from id";
        if (StringUtils.hasText(postName) || loginName != null) {
            sql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(postName)) {
            sql += " title like concat('%', :title, '%')";
            andFlag = true;
        }
        if (StringUtils.hasText(loginName)) {
            if (andFlag) {
                sql += " and";
            }
            sql += " login_id like concat('%', :login_id, '%')";
        }
        log.info("sql={}", sql);
        return template.query(sql, param, postRowMapper());
    }

    private RowMapper<Post> postRowMapper() {
        return BeanPropertyRowMapper.newInstance(Post.class); //camel 변환 지원
    }
}

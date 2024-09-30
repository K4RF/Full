package solo.blog.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcTemplateRepositoryV1 implements PostJdbcRepository {
    private final JdbcTemplate template;

    public JdbcTemplateRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Post save(Post post) {
        String sql = "insert into post(title, content, login_id) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getLoginId());
            return ps;
        }, keyHolder);
        long key = keyHolder.getKey().longValue();
        post.setId(key);
        return post;
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        String sql = "update post set title=?, content=?, login_id=? where id=?";
        template.update(sql,
                updateParam.getTitle(),
                updateParam.getContent(),
                updateParam.getLoginId(),
                postId);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "select id, title, content, login_id from post where id=?";
        try {
            Post post = template.queryForObject(sql, postRowMapper(), id);
            return Optional.of(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findAll(PostSearchCond cond) {
        String postName = cond.getTitle();
        String loginName = cond.getAuthorName();
        String sql = "select id, title, content, login_id from id";
        if (StringUtils.hasText(postName) || loginName != null) {
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(postName)) {
            sql += " title like concat('%', ?, '%')";
            param.add(postName);
            andFlag = true;
        }
        if (StringUtils.hasText(loginName)) {
            if (andFlag) {
                sql += " and";
            }
            sql += " login_id like concat('%', ?, '%')";
            param.add(loginName);
        }
        log.info("sql={}", sql);
        return template.query(sql, postRowMapper(), param.toArray());
    }

    private RowMapper<Post> postRowMapper() {
        return (rs, rowNum) -> {
            Post post = new Post();
            post.setId(rs.getLong("id"));
            post.setTitle(rs.getString("title"));
            post.setContent(rs.getString("content"));
            post.setLoginId(rs.getString("loginId"));
            return post;
        };
    }
}

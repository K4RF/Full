package solo.blog.repository.jdbc.ex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import solo.blog.entity.database.Member;

import javax.sql.DataSource;

/**
 * JdbcTemplate 사용
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {
    private final JdbcTemplate template;

    public MemberRepositoryV5(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member){
        String sql = "insert into member(member_id, login_id, name, password) values(?, ? ,? ,?)";
        template.update(sql, member.getMemberId(), member.getLoginId(), member.getName(), member.getPassword());
        return member;
    }
    @Override
    public Member findById(String memberId){
        String sql = "select * from member where member_id =?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }
    @Override
    public void update(String memberId, String loginId, String name, String password){
        String sql = "update member set password=?, name=?, login_id=? where member_id=?";
        template.update(sql, password, name, loginId, memberId);
    }
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id=?";

        template.update(sql, memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) ->{
            Member member = new Member();
            member.setMemberId(rs.getString("member_Id"));
            member.setMemberId(rs.getString("login_Id"));
            member.setMemberId(rs.getString("name"));
            member.setMemberId(rs.getString("password"));
            return member;
        };
    }
}

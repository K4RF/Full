package solo.blog.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import solo.blog.entity.database.Member;
import solo.blog.repository.jdbc.ex.MyDbException;
import solo.blog.repository.jdbc.ex.MyDuplicateKeyException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static solo.blog.h2.ConnectionConst.*;

public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave(){
        service.create("myId", "tester", "이름", "1234");
        service.create("myId", "tester", "이름", "1234");

    }
    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId, String loginId, String name, String password) {
            try {
                repository.save(new Member(memberId, loginId, name, password));
                log.info("saveId={}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                log.info("retryId={}", retryId);
                repository.save(new Member(retryId, loginId, name, password));
            } catch (MyDbException e) {
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, login_id, name, password) values(?, ?, ?, ?)";
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setString(2, member.getLoginId());
                pstmt.setString(3, member.getName());
                pstmt.setString(4, member.getPassword());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDbException(e);
            }finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }

}

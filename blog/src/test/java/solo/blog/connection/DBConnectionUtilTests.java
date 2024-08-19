package solo.blog.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import solo.blog.h2.DBConnectionUtil;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DBConnectionUtilTests {
    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}

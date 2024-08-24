package solo.blog.repository.jdbcEx;

public class MyDbException extends RuntimeException{
    public MyDbException() {
        super();
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }

}
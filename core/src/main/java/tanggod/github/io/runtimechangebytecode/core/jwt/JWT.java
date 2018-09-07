package tanggod.github.io.runtimechangebytecode.core.jwt;

public interface JWT {

    default <T> T getToken() {
        return (T) "-1";
    }

    default String getHeaderTokenKey() {
        return "accessToken";
    }
}

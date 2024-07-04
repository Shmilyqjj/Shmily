import com.google.common.base.Splitter;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Test {
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
    public static void main(String[] args) {


        String dateString = "2024-06-24 00:00:00.0".substring(0, 19);
        LocalDateTime ld = LocalDateTime.parse(dateString, formatter);
        System.out.println(ld);

    }
}


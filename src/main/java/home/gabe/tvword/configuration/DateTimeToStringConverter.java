package home.gabe.tvword.configuration;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeToStringConverter implements Converter<LocalDateTime, String> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convert(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }
}

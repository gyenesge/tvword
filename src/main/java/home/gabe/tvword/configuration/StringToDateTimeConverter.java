package home.gabe.tvword.configuration;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringToDateTimeConverter implements Converter<String, LocalDateTime> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime convert(String s) {
        return LocalDateTime.parse(s, formatter);
    }
}

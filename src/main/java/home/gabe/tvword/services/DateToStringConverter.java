package home.gabe.tvword.services;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateToStringConverter implements Converter<LocalDateTime, String> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convert(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }
}

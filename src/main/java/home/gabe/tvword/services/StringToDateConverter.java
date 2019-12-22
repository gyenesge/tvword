package home.gabe.tvword.services;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringToDateConverter implements Converter<String, LocalDateTime> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDateTime convert(String s) {
        return LocalDateTime.parse(s, formatter);
    }
}

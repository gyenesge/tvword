package home.gabe.tvword.configuration;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringToDateConverter implements Converter<String, LocalDate> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate convert(String s) {
        return LocalDate.parse(s, formatter);
    }
}

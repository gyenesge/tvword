package home.gabe.tvword.configuration;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateToStringConverter implements Converter<LocalDate, String> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convert(LocalDate localDate) {
        return formatter.format(localDate);
    }
}

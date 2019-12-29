package home.gabe.tvword.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Image {
    public final static String FORMAT_JPG = "image/jpeg";
    public final static String FORMAT_PNG = "image/png";
    public final static String FORMAT_GIF = "image/gif";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;

    private String format;

    @Lob
    private byte[] content;

    public static boolean isSupportedFormat(String format) {
        return FORMAT_GIF.equals(format) || FORMAT_JPG.equals(format) || FORMAT_PNG.equals(format);
    }
}

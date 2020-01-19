package home.gabe.tvword.idokep;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class IdokepDownloader {
    public final static String BASE_URL = "https://www.idokep.hu";
    public final static String INDEX_URL = BASE_URL + "/automata/zoldarnotert";
    public final static String IMG_PREFIX = "/automata/zoldarnotert";
    public final static String FILE_PREFIX = "target/";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private List<String> logList = new ArrayList<>();
    private String lastScheduledRun = null;

    public List<String> getImageUrls() {
        List<String> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(INDEX_URL).get();

            doc.select("img").forEach(img -> {
                String src = img.attr("src");
                if (src.startsWith(IMG_PREFIX))
                    result.add(src);
            });
        } catch (IOException ioe) {
            log.error("Unable to receive image list. ", ioe);
        }
        return result;
    }

    @Scheduled(cron = "0 * 0 ? * *")
    public void downloadPictures() {
        log.info("Idokep: Start scheduled picture download.");
        downloadPictures(getImageUrls(), "Scheduled");
    }

    public List<String> downloadPictures(List<String> urlList, String logMessage) {
        List<String> result = new ArrayList<>();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        for (String url : urlList) {
            try {
                //Open a URL Stream
                Connection.Response response = Jsoup.connect(BASE_URL + url).ignoreContentType(true).execute();

                //prepare file name
                String filename = FILE_PREFIX + timestamp + url.substring(IMG_PREFIX.length(), url.indexOf(".png")) + ".png";

                // output here
                FileOutputStream out = new FileOutputStream(new File(filename));
                out.write(response.bodyAsBytes());
                out.close();

                result.add(filename);
            } catch (IOException ioe) {
                log.error("Unable to receive image list. ", ioe);
            }
        }
        logList.add(FORMATTER.format(LocalDateTime.now()) + ": " + logMessage);

        return result;
    }

    public List<String> getLogList() {
        return logList;
    }

}

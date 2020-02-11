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

    public final static long RETRY_SECS = 55;
    public final static int RETRY_COUNT = 5;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private List<String> logList = new ArrayList<>();
    private String lastScheduledRun = null;


    @Scheduled(cron = "0 55 23 ? * *")
    public synchronized void downloadPictures() {
        log.info("Idokep: Start scheduled picture download.");
        List<String> imageUrls = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            if (i != 0) {
                log.info("No images found. Retrying in {} seconds.", RETRY_SECS);
                try {
                    this.wait(RETRY_SECS * 1000L);
                } catch (InterruptedException e) {
                    log.error("Retry period is interrupted.", e);
                }
            }
            imageUrls = getImageUrls();

            if (imageUrls != null)
                break; // image URLs found.
        }

        if (imageUrls == null) {
            //failed to get images: log and exit
            log.error("The idokep server is not accessible. No images are downloaded.");
            logList.add(FORMATTER.format(LocalDateTime.now()) + ": failed to get image URLs today.");
            return;
        }

        downloadPictures(imageUrls, "Scheduled");
    }

    public List<String> getImageUrls() {
        try {
            Document doc = Jsoup.connect(INDEX_URL).get();

            List<String> result = new ArrayList<>();
            doc.select("img").forEach(img -> {
                String src = img.attr("src");
                if (src.startsWith(IMG_PREFIX))
                    result.add(src);
            });
            return result;
        } catch (IOException ioe) {
            log.error("Unable to receive image list. ", ioe);
        }
        return null;
    }


    public List<String> downloadPictures(List<String> urlList, String logMessage) {
        List<String> result = new ArrayList<>();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        for (String url : urlList) {
            String filename = FILE_PREFIX + timestamp + url.substring(IMG_PREFIX.length(), url.indexOf(".png")) + ".png";
            try {
                //Open a URL Stream
                Connection.Response response = Jsoup.connect(BASE_URL + url).ignoreContentType(true).execute();

                // output here
                FileOutputStream out = new FileOutputStream(new File(filename));
                out.write(response.bodyAsBytes());
                out.close();

                result.add(filename);
            } catch (IOException ioe) {
                log.error("Unable to receive image: " + filename, ioe);
                logList.add(FORMATTER.format(LocalDateTime.now()) + ": failed to get " + filename);
            }
        }
        logList.add(FORMATTER.format(LocalDateTime.now()) + ": " + logMessage + " (number of files: " + result.size() + ")");

        return result;
    }

    public List<String> getLogList() {
        return logList;
    }

}

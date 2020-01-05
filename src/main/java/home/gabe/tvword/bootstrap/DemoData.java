package home.gabe.tvword.bootstrap;

import home.gabe.tvword.model.*;
import home.gabe.tvword.repositories.CampaignRepository;
import home.gabe.tvword.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class DemoData implements ApplicationListener<ContextRefreshedEvent> {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private CampaignRepository campaignRepository;

    public DemoData(PasswordEncoder passwordEncoder, UserRepository userRepository, CampaignRepository campaignRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        initData();
    }

    private Image loadImageResource(String imageUrl, String type) {
        byte[] result = null;
        try (InputStream imageStream = getClass().getResourceAsStream(imageUrl)) {
            if (imageStream == null) {
                System.err.println("Resource file is not found: " + imageUrl);
                return null;
            }

            result = imageStream.readAllBytes();
        } catch (IOException e) {
            System.err.println("Failed to read demo picture: ");
            e.printStackTrace();
        }
        Image image = new Image();
        image.setFileName(imageUrl);
        image.setFormat(type);
        image.setContent(result);
        return image;
    }

    private void assign(Display display, Campaign campaign) {
        display.getCampaigns().add(campaign);
        campaign.getDisplays().add(display);
    }

    @Transactional
    public void initData() {
        //check for existing demo data, if data exists, no need for initialization.
        if (userRepository.countByRole(UserRole.ADMIN) > 0) {
            log.info("Admin users are already initialized. No need for demo data creation.");
            return;
        }

        Display textDisplay = new Display();
        textDisplay.setName("text");
        textDisplay.setNote("Test display with text campaigns including expired ones");
        textDisplay.setHashedPassword(passwordEncoder.encode("password"));

        TextCampaign text = new TextCampaign();
        text.setName("Text campaign");
        text.setText("This is a test campaign text for display 1.");
        text.setBkgColor("#088");
        text.setTextColor("#00f");
        text.setStart(LocalDateTime.now().plus(-1, ChronoUnit.MONTHS));
        text.setExpiry(LocalDateTime.now().plus(3, ChronoUnit.MONTHS));
        assign(textDisplay, text);

        TextCampaign text2 = new TextCampaign();
        text2.setName("Másik szöveges kampány");
        text2.setText("A Mészáros és Mészáros Kft. rendelkezik radioaktív hulladéktárolókkal és nukleáris létesítményekkel kapcsolatos épületek kivitelezéséhez szükséges minősítéssel.");
        text2.setBkgColor("#700");
        text2.setTextColor("#fff");
        text2.setStart(LocalDateTime.now().plus(-1, ChronoUnit.MONTHS));
        text2.setExpiry(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        assign(textDisplay, text2);

        TextCampaign expText = new TextCampaign();
        expText.setName("Expired campaign");
        expText.setText("This is an expired campaign for display 1.");
        expText.setBkgColor("#008");
        expText.setTextColor("#f0f");
        expText.setStart(LocalDateTime.now().plus(-2, ChronoUnit.MONTHS));
        expText.setExpiry(LocalDateTime.now().plus(-1, ChronoUnit.MONTHS));
        assign(textDisplay, expText);

        TextCampaign deletedText = new TextCampaign();
        deletedText.setName("Deleted campaign");
        deletedText.setText("This is a deleted campaign for display 1.");
        deletedText.setBkgColor("#008");
        deletedText.setTextColor("#f0f");
        deletedText.setStart(LocalDateTime.now().plus(-2, ChronoUnit.MONTHS));
        deletedText.setExpiry(LocalDateTime.now().plus(-1, ChronoUnit.MONTHS));
        deletedText.setStatus(Status.DELETED);
        assign(textDisplay, deletedText);

        campaignRepository.save(text);
        campaignRepository.save(text2);
        campaignRepository.save(expText);
        campaignRepository.save(deletedText);
        userRepository.save(textDisplay);

        Display pictureDisplay = new Display();
        pictureDisplay.setName("picture");
        pictureDisplay.setNote("Test display with picture campaigns");
        pictureDisplay.setHashedPassword(passwordEncoder.encode("password"));

        PictureCampaign picture = new PictureCampaign();
        picture.setImage(loadImageResource("/demodata/martonnap.jpg", Image.FORMAT_JPG));
        picture.setName("Mártonnapi kampány");
        picture.setStart(LocalDateTime.now().plus(-1, ChronoUnit.MONTHS));
        picture.setExpiry(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));
        assign(pictureDisplay, picture);

        PictureCampaign picture2 = new PictureCampaign();
        picture2.setImage(loadImageResource("/demodata/ujbor_unnepe.jpg", Image.FORMAT_JPG));
        picture2.setName("Újbor kampány");
        picture2.setStart(LocalDateTime.now().plus(-2, ChronoUnit.MONTHS));
        picture2.setExpiry(LocalDateTime.now().plus(2, ChronoUnit.MONTHS));
        assign(pictureDisplay, picture2);
        campaignRepository.save(picture2);
        campaignRepository.save(picture);
        userRepository.save(pictureDisplay);

        Display emptyDisplay = new Display();
        emptyDisplay.setName("empty");
        emptyDisplay.setNote("Test display without any campaign");
        emptyDisplay.setHashedPassword(passwordEncoder.encode("password"));
        userRepository.save(emptyDisplay);
        //no campaigns for this display

        Display deletedDisplay = new Display();
        deletedDisplay.setName("deleted");
        deletedDisplay.setNote("Deleted display without any campaign");
        deletedDisplay.setHashedPassword(passwordEncoder.encode("password"));
        deletedDisplay.setStatus(Status.DELETED);
        userRepository.save(deletedDisplay);
        //no campaigns for this display

        //CREATE ADMINS
        userRepository.save(createAdmin("racsok", "András", "Rácsok", "password"));
        userRepository.save(createAdmin("gyenes", "Gábor", "Gyenes", "password"));

        Admin deletedAdmin = createAdmin("deluser", "Géza", "Törölt", "password");
        deletedAdmin.setStatus(Status.DELETED);
        userRepository.save(deletedAdmin);

    }

    private Admin createAdmin(String name, String firstName, String lastName, String password) {
        Admin admin1 = new Admin();
        admin1.setName(name);
        admin1.setFirstName(firstName);
        admin1.setLastName(lastName);
        admin1.setHashedPassword(passwordEncoder.encode(password));
        return admin1;
    }
}

package ma.emsi.gestionactioncharite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class GestionActionChariteApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionActionChariteApplication.class, args);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> openBrowser(
            @Value("${server.port:8080}") int port) {
        return event -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("http://localhost:" + port));
                }
            } catch (Exception e) {
                System.out.println("Could not open browser: " + e.getMessage());
            }
        };
    }
}
package ma.emsi.gestionactioncharite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.Locale;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PayPalService {

    private static final Logger log = LoggerFactory.getLogger(PayPalService.class);

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Value("${paypal.currency:EUR}")
    private String currency;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "sandbox".equals(mode)
                ? "https://api-m.sandbox.paypal.com"
                : "https://api-m.paypal.com";
    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl() + "/v1/oauth2/token",
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            log.error("PayPal auth failed: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public Map<String, Object> createOrder(double amount, String returnUrl, String cancelUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> orderBody = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "amount", Map.of(
                                "currency_code", currency,
                                "value", String.format(Locale.US, "%.2f", amount)
                        )
                )),
                "application_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl,
                        "user_action", "PAY_NOW"
                )
        );

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                    baseUrl() + "/v2/checkout/orders",
                    new HttpEntity<>(orderBody, headers),
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            log.info("PayPal order created: {}", response.getBody().get("id"));
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("PayPal createOrder failed: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public String getApprovalUrl(Map<String, Object> order) {
        List<Map<String, String>> links = (List<Map<String, String>>) order.get("links");
        return links.stream()
                .filter(l -> "approve".equals(l.get("rel")))
                .map(l -> l.get("href"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("URL d'approbation PayPal introuvable"));
    }

    public boolean captureOrder(String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                baseUrl() + "/v2/checkout/orders/" + orderId + "/capture",
                new HttpEntity<>("{}", headers),
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );
        return "COMPLETED".equals(response.getBody().get("status"));
    }
}

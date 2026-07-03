package org.atlas.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.RequestBody;

import java.io.IOException;


@Service
public class EmailService {

    private final OkHttpClient client;
    private final String apiKey;
    private final ObjectMapper objectMapper;


    public EmailService(OkHttpClient client,
                        @Value("${resend.api.key}") String apiKey,
                        ObjectMapper objectMapper
    )
    {
        this.client = client;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }


    public void sendVerificationEmail(String email, String token) {

        String html = """
            <h1>Bem vindo ao AtlasBank</h1>
            <p>Confirme seu email :):</p>

            <a href="http://localhost:8080/v1/auth/verify-email?token=%s">
                Verificar email aqui
            </a>
            """.formatted(token);


        sendEmail(
                email,
                "Verificar email",
                html

        );
    }


    public void sendEmail(String to, String subject, String html) {


        EmailRequest emailRequest = new EmailRequest(
                "noreply@vossodelivery.com.br",
                to,
                subject,
                html
        );


        String json;

        try {

            json = objectMapper.writeValueAsString(emailRequest);   // the ObjectMapper transforms Java object to JSON

        } catch (JsonProcessingException e) {

            throw new RuntimeException("Failed to create email JSON", e);

        }


        RequestBody body = RequestBody.create(      // transforming json in the Body
                json,
                MediaType.get("application/json")
        );


        Request request = new Request.Builder()
                .url("https://api.resend.com/emails")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {

                throw new RuntimeException(
                        "Email error: " + response.body().string()
                );

            }

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

}
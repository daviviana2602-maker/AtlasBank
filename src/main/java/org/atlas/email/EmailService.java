package org.atlas.email;

import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final ResendClient resendClient;


    public EmailService(ResendClient resendClient) {
        this.resendClient = resendClient;
    }


    public void sendVerificationEmail(String email, String token) {

        String html = """
            <h1>Bem vindo ao AtlasBank</h1>
            <p>Por gentileza, confirme seu email ;)</p>

            <a href="http://localhost:8080/v1/auth/verify-email?token=%s">
                Verificar email aqui
            </a>
            """.formatted(token);


        resendClient.sendEmail(
                email,
                "Verificar email",
                html

        );
    }


    public void sendEmailUserPassword(String email, String token) {

        String html = """
            <h1>AtlasBank aqui!</h1>
            <p>Por gentileza, confirme sua nova senha do usuário ;)</p>

            <a href="http://localhost:8080/v1/auth/verify-user-password?token=%s">
                Validar senha aqui
            </a>
            """.formatted(token);


        resendClient.sendEmail(
                email,
                "Validar senha do usuário aqui",
                html

        );
    }


    public void sendEmailAccountPassword(String email, String token) {

        String html = """
            <h1>AtlasBank aqui!</h1>
            <p>Por gentileza, confirme a nova senha da sua conta atlas ;)</p>

            <a href="http://localhost:8080/v1/auth/verify-account-password?token=%s">
                Validar senha da conta aqui
            </a>
            """.formatted(token);


        resendClient.sendEmail(
                email,
                "Validar senha da conta aqui",
                html

        );
    }


}
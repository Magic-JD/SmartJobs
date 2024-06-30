package integration.controller;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.entity.EntityUtils;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicNameValuePair;
import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static constants.HtmlConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerIT extends IntegrationTest {

    @Autowired
    CredentialDal credentialDal;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    void testLoginDirectsYouToTheLoginPageAndYouDontHaveToBeLoggedInToViewThisPage() throws Exception {
        getMockMvc().perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(matchesHtml(LOGIN_PAGE));
    }

    @Test
    void testLoginWillShowAnErrorIfTheUrlParameterContainsAMistake() throws Exception {
        getMockMvc().perform(get("/login?error"))
                .andExpect(status().isOk())
                .andExpect(matchesHtml(LOGIN_PAGE_WITH_ERROR));
    }

    @Test
    void testRegisterWillShowTheRegistrationPageForTheUserWithTheExpectedForm() throws Exception {
        getMockMvc().perform(get("/login/register").headers(HTTP_HEADERS))
                .andExpect(status().isOk())
                .andExpect(matchesHtml(REGISTRATION_FORM));
    }


    @Test
    void testRegistrationWillRegisterANonExistingUserAndAllowSignup() throws Exception {
        assertTrue(credentialDal.getUser("username2").isEmpty());
        getMockMvc().perform(post("/login/registration")
                        .headers(HTTP_HEADERS)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("username", "username2"),
                                new BasicNameValuePair("password", "password2"),
                                new BasicNameValuePair("matchingPassword", "password2")
                        ))))
                ).andExpect(status().isOk())
                .andExpect(header().string("HX-Redirect", "/login"));

        User user = credentialDal.getUser("username2").orElseThrow();
        assertEquals("username2", user.getUsername());
        assertTrue(passwordEncoder.matches("password2", user.getPassword()));
    }

    @Test
    void testRegistrationWillRejectAPreexistingUser() throws Exception {
        getMockMvc().perform(post("/login/registration")
                        .headers(HTTP_HEADERS)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                                new BasicNameValuePair("username", "username"),
                                new BasicNameValuePair("password", "password2"),
                                new BasicNameValuePair("matchingPassword", "password2")
                        ))))
                ).andExpect(status().isOk())
                .andExpect(matchesHtml(REGISTRATION_WITH_ERRORS));
    }
}

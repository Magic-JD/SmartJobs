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


    private static final String LOGIN_PAGE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta content="width=device-width, initial-scale=1.0" name="viewport">
                <title>Smart Jobs</title>
                <link rel="stylesheet" href="/styles/style.css"/>
                <script crossorigin="anonymous"
                        integrity="sha384-0gxUXCCR8yv9FM2b+U3FDbsKthCI66oH5IA9fHppQq9DDMHuMauqq1ZHBpJxQ0J0"
                        src="https://unpkg.com/htmx.org@1.9.11"></script>
                <script src="https://unpkg.com/htmx.org@1.9.11/dist/ext/sse.js"></script>
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@100..900&display=swap" rel="stylesheet">
            </head>
            <link rel="shortcut icon" type="../image/png" href="/favicon/favicon.ico"/>
            <body id="mainBody" class="background wrapper">
            <header class="primary-header flex-container highlight">
                <a class="text-medium icon-link" href="/">Smart Jobs</a>
                <nav class="primary-navigation flex-container right-pushed">
                    
                    
                    <a class="button-light right-pushed button-header-light" href="/login">Login</a>
                </nav>
                <div></div>
                
            </header>
            <main>
                <div class="top-padding"></div>
                <div class="text-medium right-nudged flex-stack">
                    <h1 class="text-large">Login</h1>
                    <form name='f' action="login" method='POST'>
                        <div class="flex-stack">
                            <label>
                                <input type='text' name='username' value='' placeholder="Username">
                            </label>
                            <label>
                                <input type='password' name='password' placeholder="Password" />
                            </label>
                        </div>
                        <input name="submit" type="submit" value="Submit" class="button-light button-enlarge" />
                        <button class="button-light button-enlarge"
                                hx-get="/login/register"
                                hx-swap="innerHtml"
                                hx-target="closest main"
                                hx-trigger="click"
                        >Register</button>
                    </form>
                </div>
            </main>
            <footer class="primary-footer flex-container highlight">
                <div class="social-media-area text-medium"><p>Smart Jobs</p></div>
                <div class="right-pushed contact-us"><p class="text-medium">Contact Us</p>
                    <p class="text-small tagline">support@secretsauce.site</p></div>
            </footer>
                </body>
            </html>""";


    private static final String LOGIN_PAGE_WITH_ERROR = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta content="width=device-width, initial-scale=1.0" name="viewport">
                <title>Smart Jobs</title>
                <link rel="stylesheet" href="/styles/style.css"/>
                <script crossorigin="anonymous"
                        integrity="sha384-0gxUXCCR8yv9FM2b+U3FDbsKthCI66oH5IA9fHppQq9DDMHuMauqq1ZHBpJxQ0J0"
                        src="https://unpkg.com/htmx.org@1.9.11"></script>
                <script src="https://unpkg.com/htmx.org@1.9.11/dist/ext/sse.js"></script>
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Heebo:wght@100..900&display=swap" rel="stylesheet">
            </head>
            <link rel="shortcut icon" type="../image/png" href="/favicon/favicon.ico"/>
            <body id="mainBody" class="background wrapper">
            <header class="primary-header flex-container highlight">
                <a class="text-medium icon-link" href="/">Smart Jobs</a>
                <nav class="primary-navigation flex-container right-pushed">
                    
                    
                    <a class="button-light right-pushed button-header-light" href="/login">Login</a>
                </nav>
                <div></div>
                <div id="message" class="error-message text-medium">Username or password is incorrect.</div>
            </header>
            <main>
                <div class="top-padding"></div>
                <div class="text-medium right-nudged flex-stack">
                    <h1 class="text-large">Login</h1>
                    <form name='f' action="login" method='POST'>
                        <div class="flex-stack">
                            <label>
                                <input type='text' name='username' value='' placeholder="Username">
                            </label>
                            <label>
                                <input type='password' name='password' placeholder="Password" />
                            </label>
                        </div>
                        <input name="submit" type="submit" value="Submit" class="button-light button-enlarge" />
                        <button class="button-light button-enlarge"
                                hx-get="/login/register"
                                hx-swap="innerHtml"
                                hx-target="closest main"
                                hx-trigger="click"
                        >Register</button>
                    </form>
                </div>
            </main>
            <footer class="primary-footer flex-container highlight">
                <div class="social-media-area text-medium"><p>Smart Jobs</p></div>
                <div class="right-pushed contact-us"><p class="text-medium">Contact Us</p>
                    <p class="text-small tagline">support@secretsauce.site</p></div>
            </footer>
                </body>
            </html>""";

    private static final String REGISTRATION_FORM = """
            <div class="top-padding"></div>
            <div class="text-medium flex-stack">
                <h1 class="text-large">Register</h1>
                <form class = flex-stack>
                    <label>
                        <input id="username" placeholder="Username" type="text" name="username" value=""/>
                    </label>
                    <label>
                        <input id="password" placeholder="Password" type="password" name="password" value=""/>
                    </label>
                    <label>
                        <input id="matchingPassword" placeholder="Confirm Password" type="password" name="matchingPassword" value=""/>
                    </label>
                    <button class="button-light button-enlarge" hx-post="/login/registration"
                        hx-swap="innerHtml"
                        hx-target="closest main"
                        hx-trigger="click"
                        type="submit"
                    >Submit
                    </button>
                </form>
                
            </div>""";

    private static final String REGISTRATION_WITH_ERRORS = """
            <div class="top-padding"></div>
                <div class="text-medium flex-stack">
                    <h1 class="text-large">Register</h1>
                    <form class = flex-stack>
                        <label>
                            <input id="username" placeholder="Username" type="text" name="username" value="username"/>
                        </label>
                        <label>
                            <input id="password" placeholder="Password" type="password" name="password" value=""/>
                        </label>
                        <label>
                            <input id="matchingPassword" placeholder="Confirm Password" type="password" name="matchingPassword" value=""/>
                        </label>
                        <button class="button-light button-enlarge" hx-post="/login/registration"
                                hx-swap="innerHtml"
                                hx-target="closest main"
                                hx-trigger="click"
                                type="submit"
                        >Submit
                        </button>
                    </form>
                    <p>An account for that username/email already exists</p>
                </div>""";

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

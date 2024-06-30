package integration.controller;

import integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

class PageControllerIT extends IntegrationTest {

    private static final String UNLOGGED_LANDING_PAGE = """
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
            <link rel="shortcut icon" type="image/png" href="/favicon/favicon.ico"/>
            <body id="mainBody" class="background wrapper">
            <header class="primary-header flex-container highlight">
                <a class="text-medium icon-link" href="/">Smart Jobs</a>
                <nav class="primary-navigation flex-container right-pushed">
                    <a class="button-light right-pushed button-header-light" href="/login">Login</a>
                </nav>
                <div></div>
                <div id="message" class="attacher"></div>
            </header>
            <main>
                <div class="top-padding"></div>
                <div>
                    <div class="heading">
                        <h1 class="landing-text text-xl">Smart Jobs</h1>
                        <p class="landing-text text-medium tagline">Simple. Secure. Smarter</p>
                    </div>
                    <div class="heading landing-text text-small">
                        <p class="spaced-paragraph">Welcome to Smart Jobs, the next-generation recruitment platform that
                            revolutionizes the way you find and hire top talent. With our cutting-edge AI technology, recruiters can
                            now streamline the candidate screening process like never before, saving time, effort, and resources
                            while identifying the perfect fit for every position.
                        </p>
                        <p class="spaced-paragraph">
                        <h3 class="text-medium">Efficiency Redefined</h3>
                        <snippet>
                            Gone are the days of manually sifting through countless resumes and cover letters. With Smart Jobs,
                            recruiters can upload CVs and let our AI algorithms do the heavy lifting. Our intelligent system swiftly
                            scans and analyzes applicant data, extracting key information such as skills, qualifications, and
                            experience with unmatched accuracy and speed.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium">Precision Selection</h3>
                        <snippet>
                            Say goodbye to guesswork and hello to data-driven decision-making. Smart Jobs utilizes advanced machine
                            learning capabilities to intelligently rank and categorize candidates based on their suitability for the
                            role. By identifying the most promising candidates upfront, recruiters can focus their efforts on
                            engaging with the best prospects, leading to faster and more successful hires.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium">Bias-Free Recruitment </h3>
                        <snippet>
                            Diversity and inclusion are at the core of our mission. Smart Jobs helps mitigate unconscious bias by
                            evaluating candidates solely on their qualifications and potential, regardless of demographic background
                            or personal characteristics. By promoting fairness and objectivity in the hiring process, we empower
                            organizations to build more diverse and high-performing teams.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium"> Insightful Analytics </h3>
                        <snippet>
                            Empower your recruitment strategy with actionable insights and comprehensive analytics. Smart Jobs
                            provides real-time visibility into recruitment metrics, allowing recruiters to track key performance
                            indicators such as time-to-fill, cost-per-hire, and candidate quality. With our intuitive dashboard, you
                            can make informed decisions, identify areas for improvement, and optimize your hiring process for
                            maximum efficiency.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium"> Get Started Today </h3>
                        <snippet>
                            Join the ranks of leading organizations that trust Smart Jobs to transform their recruitment efforts.
                            Whether you're a small business looking to expand your team or a large enterprise seeking top talent,
                            our AI-powered platform is your ultimate recruitment solution. Sign up now and experience the future of
                            hiring with Smart Jobs.
                        </snippet>
                        </p>
                    </div>
                </div>
            </main>
            <footer class="primary-footer flex-container highlight">
                <div class="social-media-area text-medium"><p>Smart Jobs</p></div>
                <div class="right-pushed contact-us"><p class="text-medium">Contact Us</p>
                    <p class="text-small tagline">support@secretsauce.site</p></div>
            </footer>
                </body>
            </html>
            """;

    public static final String LOGGED_LANDING_PAGE = """
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
            <link rel="shortcut icon" type="image/png" href="/favicon/favicon.ico"/>
            <body id="mainBody" class="background wrapper">
            <header class="primary-header flex-container highlight">
                <a class="text-medium icon-link" href="/">Smart Jobs</a>
                <nav class="primary-navigation flex-container right-pushed">
                    <div class="text-small left-nudged">
                        <div>username</div>
                    </div>
                    <div class="text-small left-nudged"
                         hx-ext="sse"
                         sse-connect="/sse/register"
                         sse-swap="credit"
                         hx-swap="innerHtml"
                         hx-target="this"
                         hx-trigger="load"
                    >
                        <div>Credit: 0</div>
                    </div>
                    <a class="button-dark right-pushed button-header-dark" hx-swap="innerHtml"
                       hx-target="next main"
                       hx-trigger="click"
                       hx-get="/roles">Roles</a><a class="button-dark right-pushed button-header-dark" hx-swap="innerHtml"
                       hx-target="next main"
                       hx-trigger="click"
                       hx-get="/candidates">Candidates</a>
                    <a class="button-light right-pushed button-header-light" href="/logout">Logout</a>
                </nav>
                <div id="message-trigger"
                 class="attacher"
                 hx-ext="sse"
                 hx-swap="outerHtml"
                 hx-target="#message"
                 sse-connect="/sse/register"
                 sse-swap="message"
            ></div>
                <div id="message" class="attacher"></div>
            </header>
            <main>
                <div class="top-padding"></div>
                <div>
                    <div class="heading">
                        <h1 class="landing-text text-xl">Smart Jobs</h1>
                        <p class="landing-text text-medium tagline">Simple. Secure. Smarter</p>
                    </div>
                    <div class="heading landing-text text-small">
                        <p class="spaced-paragraph">Welcome to Smart Jobs, the next-generation recruitment platform that
                            revolutionizes the way you find and hire top talent. With our cutting-edge AI technology, recruiters can
                            now streamline the candidate screening process like never before, saving time, effort, and resources
                            while identifying the perfect fit for every position.
                        </p>
                        <p class="spaced-paragraph">
                        <h3 class="text-medium">Efficiency Redefined</h3>
                        <snippet>
                            Gone are the days of manually sifting through countless resumes and cover letters. With Smart Jobs,
                            recruiters can upload CVs and let our AI algorithms do the heavy lifting. Our intelligent system swiftly
                            scans and analyzes applicant data, extracting key information such as skills, qualifications, and
                            experience with unmatched accuracy and speed.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium">Precision Selection</h3>
                        <snippet>
                            Say goodbye to guesswork and hello to data-driven decision-making. Smart Jobs utilizes advanced machine
                            learning capabilities to intelligently rank and categorize candidates based on their suitability for the
                            role. By identifying the most promising candidates upfront, recruiters can focus their efforts on
                            engaging with the best prospects, leading to faster and more successful hires.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium">Bias-Free Recruitment </h3>
                        <snippet>
                            Diversity and inclusion are at the core of our mission. Smart Jobs helps mitigate unconscious bias by
                            evaluating candidates solely on their qualifications and potential, regardless of demographic background
                            or personal characteristics. By promoting fairness and objectivity in the hiring process, we empower
                            organizations to build more diverse and high-performing teams.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium"> Insightful Analytics </h3>
                        <snippet>
                            Empower your recruitment strategy with actionable insights and comprehensive analytics. Smart Jobs
                            provides real-time visibility into recruitment metrics, allowing recruiters to track key performance
                            indicators such as time-to-fill, cost-per-hire, and candidate quality. With our intuitive dashboard, you
                            can make informed decisions, identify areas for improvement, and optimize your hiring process for
                            maximum efficiency.
                        </snippet>
                        </p><p class="spaced-paragraph">
                        <h3 class="text-medium"> Get Started Today </h3>
                        <snippet>
                            Join the ranks of leading organizations that trust Smart Jobs to transform their recruitment efforts.
                            Whether you're a small business looking to expand your team or a large enterprise seeking top talent,
                            our AI-powered platform is your ultimate recruitment solution. Sign up now and experience the future of
                            hiring with Smart Jobs.
                        </snippet>
                        </p>
                    </div>
                </div>
            </main>
            <footer class="primary-footer flex-container highlight">
                <div class="social-media-area text-medium"><p>Smart Jobs</p></div>
                <div class="right-pushed contact-us"><p class="text-medium">Contact Us</p>
                    <p class="text-small tagline">support@secretsauce.site</p></div>
            </footer>
                </body>
            </html>
                        
            """;

    @Test
    void testPageControllerReturnsTheLandingPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(matchesHtml(UNLOGGED_LANDING_PAGE));
    }

    @Test
    void testPageControllerReturnsTheLoggedInLandingPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(matchesHtml(LOGGED_LANDING_PAGE));
    }


    @Test
    void testPageControllerReturnsTheRolesPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/roles").with(user(USER)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testPageControllerReturnsTheCandidatesPage() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.get("/candidates").with(user(USER)))
                .andExpect(MockMvcResultMatchers.content().string("""
                        <div class="top-padding"></div>
                        <div class="flex-container">
                            <div id="info-box" class="flex-stack right-nudged text-only"
                             hx-get="/candidate/number/selected"
                             hx-swap="outerHTML"
                             hx-target="this"
                             hx-trigger="candidate-count-updated from:body">
                            <p class="text-only">Role: role</p>
                            <p class="text-only">Selected Candidates: 1</p>
                        </div>

                            <a class="button-dark button-enlarge right-pushed"
                               hx-get="/upload"
                               hx-swap="innerHtml"
                               hx-target="closest main"
                               hx-trigger="click"
                            >Upload Additional Candidates</a>
                            <a class="button-light button-enlarge right-pushed"
                               hx-get="/analyze"
                               hx-swap="innerHtml"
                               hx-target="closest main"
                               hx-trigger="click"
                            >Analyze Selected Candidates</a>
                        </div>
                        <div class="loading-container" hx-get="/candidate" hx-swap="outerHTML" hx-target="#table-placeholder"
                             hx-trigger="load"
                             id="table-placeholder">
                            <h1 class="text-large">Loading Candidates</h1>
                            <img alt="loading" class='htmx-indicator' src="/images/loading.gif"/>
                        </div>

                                        """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}

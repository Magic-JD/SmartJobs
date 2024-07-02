package constants;

public class HtmlConstants {

    private HtmlConstants() {
        // Private Construction to prevent instantiation.
    }


    public static final String TABLE_HEADING = """
            <div id="uploaded" hx-target="closest div" hx-swap="outerHTML"
                 class="candidate-table flex-stack text-medium">
            <div class="candidate-row flex-container">
                    <p class="candidate-display-left">Name</p>
                    <div class="flex-container right-pushed">
                        <button class="button-light button-enlarge candidate-display-right"
                                hx-put="/candidate/select/all?select=true"
                                hx-target="#uploaded"
                                hx-swap="outerHTML"
                        >Select All
                        </button>
                        <button class="button-dark button-enlarge candidate-display-right"
                                hx-delete="/candidate/delete/all"
                                hx-target="#uploaded"
                                hx-swap="outerHTML"
                        >Delete Selected
                        </button>
                    </div>
                </div>""";


    public static final String CANDIDATE_1_SELECTED = """
            <div class="candidate-row flex-container">
                <p class="candidate-display-left">Joe</p>
                <button class='candidate-display-right button-light'
                        hx-swap="innerHTML" hx-put="/candidate/select/1?select=false">Selected</button>
                <button class='button-dark' hx-delete="/candidate/delete/1">Delete
                </button>
            </div>""";

    public static final String CANDIDATE_1_UNSELECTED = """
                <div class="candidate-row flex-container">
                    <p class="candidate-display-left">Joe</p>
                    <button class='candidate-display-right button-dark'
                            hx-swap="innerHTML" hx-put="/candidate/select/1?select=true">Select</button>
                    <button class='button-dark' hx-delete="/candidate/delete/1">Delete
                    </button>
                </div>
            """;

    public static final String CANDIDATE_2_SELECTED = """
            <div class="candidate-row flex-container">
                    <p class="candidate-display-left">james</p>
                    <button class='candidate-display-right button-light'
                            hx-swap="innerHTML" hx-put="/candidate/select/2?select=false">Selected</button>
                    <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                    </button>
                </div>
            """;

    public static final String CANDIDATE_2_UNSELECTED = """
            <div class="candidate-row flex-container">
                <p class="candidate-display-left">james</p>
                <button class='candidate-display-right button-dark'
                        hx-swap="innerHTML" hx-put="/candidate/select/2?select=true">Select</button>
                <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                </button>
            </div>
            """;
    public static final String CANDIDATES_TABLE_DEFAULT = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_1_SELECTED }
                \{ CANDIDATE_2_UNSELECTED }
            </div>
                                    """ ;

    public static final String CANDIDATES_TABLE_DELETED = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_2_UNSELECTED }
            </div>
                                    """ ;

    public static final String CANDIDATES_TABLE_ALL_UNSELECTED = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_1_UNSELECTED }
                \{ CANDIDATE_2_UNSELECTED }
            </div>
                                    """ ;


    public static final String CANDIDATES_TABLE_ALL_SELECTED = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_1_SELECTED }
                \{ CANDIDATE_2_SELECTED }
            </div>
                                    """ ;

    public static final String UNLOGGEDIN_HEADER = """
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
                </nav> """;


    public static final String LOGIN_PAGE = STR. """
            \{ UNLOGGEDIN_HEADER }
                <div></div>
            </header>
            <main>
                <div class="top-padding"></div>
                <div class="text-medium right-nudged flex-stack">
                    <h1 class="text-large">Login</h1>
                    <form name='f' action="login" method='POST'>
                        <div class="flex-stack">
                            <label>
                                <input type='text' name='username' value='' placeholder="Email">
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
            </html>""" ;


    public static final String LOGIN_PAGE_WITH_ERROR = STR. """
                \{ UNLOGGEDIN_HEADER }
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
                                <input type='text' name='username' value='' placeholder="Email">
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
            </html>""" ;

    public static final String REGISTRATION_FORM = """
            <div class="top-padding"></div>
            <div class="text-medium flex-stack">
                <h1 class="text-large">Register</h1>
                <form class = flex-stack>
                    <label>
                        <input id="email" placeholder="Email" type="text" name="email" value=""/>
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

    public static final String VERIFY_EMAIL = """
            <div class="top-padding"></div>
            <div class="text-large flex-stack">
                Please click on the link that has been sent to your email to verify your account.
            </div>""";

    public static final String REGISTRATION_WITH_ERRORS = """
            <div class="top-padding"></div>
                <div class="text-medium flex-stack">
                    <h1 class="text-large">Register</h1>
                    <form class = flex-stack>
                        <label>
                            <input id="email" placeholder="Email" type="text" name="email" value="email@email.com"/>
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
                    <p>An account for that email already exists</p>
                </div>""";


    public static final String LANDING_PAGE_TEXT = """
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
            </html>""";

    public static final String UNLOGGED_LANDING_PAGE = STR. """
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
            \{ LANDING_PAGE_TEXT }
            """ ;

    public static final String LOGGED_LANDING_PAGE = STR. """
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
                        <div>email@email.com</div>
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
                       hx-get="/roles">Roles</a>
                    <a class="button-dark right-pushed button-header-dark" hx-swap="innerHtml"
                       hx-target="next main"
                       hx-trigger="click"
                       hx-get="/candidates">Candidates
                    </a>
                    <a class="button-dark right-pushed button-header-dark" hx-swap="innerHtml"
                       hx-target="next main"
                       hx-trigger="click"
                       hx-get="/credit">Credit
                    </a>
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
            \{ LANDING_PAGE_TEXT }
                        
            """ ;

    public static final String CANDIDATE_PAGE = """
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
            </div>""";
}

package constants;

public class HtmlConstants {

    private HtmlConstants() {
        // Private Construction to prevent instantiation.
    }

    public static final String FOOTER = STR."""
<footer class="primary-footer flex-container highlight">
    <div class="social-media-area text-medium"><p>Smart Jobs</p></div>
    <div class="right-pushed contact-us"><p class="text-medium">Contact Us</p>
        <p class="text-small tagline">support@secretsauce.site</p></div>
</footer>
            """;


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
                    <p class="candidate-display-left">James</p>
                    <button class='candidate-display-right button-light'
                            hx-swap="innerHTML" hx-put="/candidate/select/2?select=false">Selected</button>
                    <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                    </button>
                </div>
            """;

    public static final String CANDIDATE_2_UNSELECTED = """
            <div class="candidate-row flex-container">
                <p class="candidate-display-left">James</p>
                <button class='candidate-display-right button-dark'
                        hx-swap="innerHTML" hx-put="/candidate/select/2?select=true">Select</button>
                <button class='button-dark' hx-delete="/candidate/delete/2">Delete
                </button>
            </div>
            """;
    public static final String CANDIDATES_TABLE_DEFAULT = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_2_UNSELECTED }
                \{ CANDIDATE_1_SELECTED }
            </div>
                                    """ ;

    public static final String CANDIDATES_TABLE_DELETED = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_2_UNSELECTED }
            </div>
                                    """ ;

    public static final String CANDIDATES_TABLE_ALL_UNSELECTED = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_2_UNSELECTED }
                \{ CANDIDATE_1_UNSELECTED }
            </div>
                                    """ ;


    public static final String CANDIDATES_TABLE_ALL_SELECTED = STR. """
                \{ TABLE_HEADING }
                \{ CANDIDATE_2_SELECTED }
                \{ CANDIDATE_1_SELECTED }
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
    <link href="https://fonts.googleapis.com/css2?family=Work+Sans:ital,wght@0,100..900;1,100..900" rel="stylesheet">
</head>
<link rel="shortcut icon" type="image/png" href="/favicon/favicon.ico"/>
<body id="mainBody" class="background wrapper">
<header class="primary-header flex-container highlight">
    <a class="text-small icon-link" href="/">
        <img alt="Mobius Logo" class="logo-image" src="/images/mobius_logo.png"> <img alt="Smart Jobs Logo"
                                                                                            class="logo-image"
                                                                                            src="/images/smart_jobs_logo.png">
    </a>
    <nav class="primary-navigation flex-container right-pushed">
        <div class="right-pushed">
            <a class="header-info button-header" href="/login">Login</a>
        </div>
                </nav>""";


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
                    <input type='password' name='password' placeholder="Password"/>
                </label>
            </div>
            <input name="submit" type="submit" value="Submit" class="button-dark button-enlarge"/>
            <button class="button-dark button-enlarge"
                    hx-get="/login/register"
                    hx-swap="innerHtml"
                    hx-target="closest main"
                    hx-trigger="click"
            >Register
            </button>
        </form>
    </div>
</main>
\{FOOTER}
</body>
</html>
            """;


    public static final String LOGIN_PAGE_WITH_ERROR = STR. """
                \{ UNLOGGEDIN_HEADER }
                    <div></div>
                    <div id="message" class="text-medium error-message">Username or password is incorrect.
                    </div>
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
                                    <input type='password' name='password' placeholder="Password"/>
                                </label>
                            </div>
                            <input name="submit" type="submit" value="Submit" class="button-dark button-enlarge"/>
                            <button class="button-dark button-enlarge"
                                    hx-get="/login/register"
                                    hx-swap="innerHtml"
                                    hx-target="closest main"
                                    hx-trigger="click"
                            >Register
                            </button>
                        </form>
                    </div>
                </main>
                \{FOOTER}
                </body>
            </html>
            """ ;

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


    public static final String LANDING_PAGE_TEXT = STR. """
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
            <div class="info-area">
                <h3 class="text-medium">How to get started</h3>
                <div class="info">
                    Ready to start analyzing candidates? The guide below will take you through it step-by-step.
                </div>
                <h3 class="text-medium">Set up a Role</h3>
                <div class="info">
                    You can start by creating a new role and customising your criteria on the <a hx-get="/roles"
                                                                                                 hx-swap="innerHtml"
                                                                                                 hx-target="closest main"
                                                                                                 hx-trigger="click"
                                                                                                 class="highlighted">Roles</a>
                    page.
                    Select a criteria you'd like to add and choose your max score - this is the weighting you apply to
                    the criteria. The more important the criteria, the higher the max score!
                    <br/>
                    <br/>
                    You can apply up to 10 criteria per role. For a summary of the scoring rules for each criteria, just
                    hover over the information symbol next to it.
                </div>
                <h3 class="text-medium">Add Candidates</h3>
                <div class="info">
                    Once you're happy with your role criteria, jump to the <a hx-get="/candidates" hx-swap="innerHtml"
                                                                              hx-target="closest main"
                                                                              hx-trigger="click" class="highlighted">Candidates</a>
                    page, where you can upload resumes. Resumes uploaded into the system are anonymised and any
                    information which might indicate age, ethnicity or gender is removed (don't worry, you'll still be
                    able to see their name).
                    <br/>
                    <br/>
                    Start by running just a few - you may want to go back and tweak your criteria.
                </div>
                <h3 class="text-medium">Analyze</h3>
                <div class="info">
                    From the <a hx-get="/candidates" hx-swap="innerHtml" hx-target="closest main" hx-trigger="click"
                                class="highlighted">Candidates</a> page, select the candidates you'd like to analyse and
                    click 'Analyze Selected Candidates'.
                    <br/>
                    <br/>
                    The analysis should take just a few seconds, then you'll be able to see your overall scores and
                    score breakdowns, as well as an explanation for why that score was awarded.
                </div>
                <h3 class="text-medium">How do credits work?</h3>
                <div class="info">
                    One credit is used each time a candidate is uploaded or analysed.
                </div>
                <h3 class="text-medium">Want more free credits?</h3>
                <div class="info">
                    Join our free beta trial! Email us at support@secretsauce.site
                </div>
                <h3 class="text-medium">Do you integrate with any applicant tracking systems?</h3>
                <div class="info">
                    Not just yet, but watch this space! We're still in beta, so we're not quite at the level of the
                    finished product just yet.
                    <br/>
                    <br/>
                    As we are in beta, any loss of free credits through bugs or accident will not be returned.
                </div>
            </div>
        </div>
    </div>
</main>
    \{FOOTER}
    </body>
</html>""";

    public static final String UNLOGGED_LANDING_PAGE = STR. """
            \{ UNLOGGEDIN_HEADER }
                <div></div>
                <div class="attacher" id="message"></div>
            </header>
            \{ LANDING_PAGE_TEXT }
            """ ;

    public static final String LOGGED_IN_HEADER = STR."""
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
                <link href="https://fonts.googleapis.com/css2?family=Work+Sans:ital,wght@0,100..900;1,100..900" rel="stylesheet">
            </head>
<link rel="shortcut icon" type="image/png" href="/favicon/favicon.ico"/>
<body id="mainBody" class="background wrapper">
<header class="primary-header flex-container highlight">
    <a class="text-small icon-link" href="/">
        <img alt="Mobius Logo" class="logo-image" src="/images/mobius_logo.png"> <img alt="Smart Jobs Logo"
                                                                                            class="logo-image"
                                                                                            src="/images/smart_jobs_logo.png">
    </a>
    <nav class="primary-navigation flex-container right-pushed">
        <div class="right-pushed">
            <div class="header-info credit">
                <img alt="Credits" class="credit-image" src="/images/credit.png">
                <span hx-ext="sse"
                      hx-swap="innerHtml"
                      hx-target="this"
                      hx-trigger="load"
                      sse-connect="/sse/register"
                      sse-swap="credit"
                >0</span>
            </div>
            <a class="header-info button-header"
               hx-swap="innerHtml"
               hx-target="next main"
               hx-trigger="click"
               hx-get="/roles">Roles</a><a class="header-info button-header"
               hx-swap="innerHtml"
               hx-target="next main"
               hx-trigger="click"
               hx-get="/candidates">Candidates</a><a class="header-info button-header"
               hx-swap="innerHtml"
               hx-target="next main"
               hx-trigger="click"
               hx-get="/credit">Credit</a>
            <div class="header-info button-header" onclick="toggleDropdown('accountDropdown')">Account
    <div id="accountDropdown" class="dropdown text-small" hidden="hidden">
        <ul>
            <li>
                <a href="/logout">Logout</a>
            </li>
        </ul>
    </div>
</div>

<script>
    function toggleDropdown(dropdownId) {
        const dropdown = document.getElementById(dropdownId);

        if (dropdown) {
            if (dropdown.hasAttribute('hidden')) {
                dropdown.removeAttribute('hidden');
            } else {
                dropdown.setAttribute('hidden', 'hidden');
            }
        }
    }
</script>

        </div>
    </nav>
    <div id="message-trigger"
     class="attacher"
     hx-ext="sse"
     hx-swap="outerHtml"
     hx-target="#message"
     sse-connect="/sse/register"
     sse-swap="message"
></div>
    <div class="attacher" id="message"></div>
</header>
            """;

    public static final String LOGGED_LANDING_PAGE = STR. """
            \{LOGGED_IN_HEADER}
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
                   hx-swap="outerHtml"
                   hx-target="#upload-files"
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
            <div id="upload-files"></div>
            """;
}

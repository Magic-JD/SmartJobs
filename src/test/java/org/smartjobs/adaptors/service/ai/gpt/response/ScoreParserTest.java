package org.smartjobs.adaptors.service.ai.gpt.response;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.adaptors.service.ai.gpt.entity.Pass;
import org.smartjobs.adaptors.service.ai.gpt.entity.UnadjustedScore;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class ScoreParserTest {

    public static final Optional<UnadjustedScore> EXPECTED_SCORE = Optional.of(new UnadjustedScore("Description.", 7.5));
    public static final Optional<UnadjustedScore> EMPTY_SCORE = Optional.empty();
    public static final Optional<Pass> EXPECTED_PASS_TRUE = Optional.of(new Pass("Description.", true));
    public static final Optional<Pass> EXPECTED_PASS_FALSE = Optional.of(new Pass("Description.", false));
    public static final Optional<Pass> EMPTY_PASS = Optional.empty();
    private final ScoreParser scoreParser = new ScoreParser();

    @Test
    void testParseScoreShouldParseACorrectlyFormattedScore() {
        assertEquals(EXPECTED_SCORE, scoreParser.parseScore("Description. SCORE 7.5"));
    }

    @Test
    void testParseScoreShouldParseAStringWithAnUnexpectedFullStopAtTheEnd() {
        assertEquals(EXPECTED_SCORE, scoreParser.parseScore("Description. SCORE 7.5."));
    }

    @Test
    void testParseScoreShouldParseAStringWithUnexpectedCharacters() {
        assertEquals(EXPECTED_SCORE, scoreParser.parseScore("Description. SCORE is 7.5! well done."));
    }

    @Test
    void testParseScoreShouldParseAStringWithUnexpectedSpaces() {
        assertEquals(EXPECTED_SCORE, scoreParser.parseScore("Description. SCORE      7.5   "));
    }

    @Test
    void testParseScoreShouldReturnEmptyIfTheWordScoreIsNotInTheResponse() {
        assertEquals(EMPTY_SCORE, scoreParser.parseScore("Description. The result is 7.5! well done."));
    }

    @Test
    void testParseScoreShouldReturnEmptyIfTheResultNumberCannotBeParsed() {
        assertEquals(EMPTY_SCORE, scoreParser.parseScore("Description. SCORE 7..5"));
    }

    @Test
    void testParsePassTrueShouldReturnTheExpectedValue() {
        assertEquals(EXPECTED_PASS_TRUE, scoreParser.parsePass("Description. PASS true"));
    }

    @Test
    void testParsePassFalseShouldReturnTheExpectedValue() {
        assertEquals(EXPECTED_PASS_FALSE, scoreParser.parsePass("Description. PASS false"));
    }

    @Test
    void testParsePassShouldParseAStringWithAnUnexpectedFullStopAtTheEnd() {
        assertEquals(EXPECTED_PASS_TRUE, scoreParser.parsePass("Description. PASS true."));
    }

    @Test
    void testParseScoreShouldParseAStringWithUnexpectedCasing() {
        assertEquals(EXPECTED_PASS_TRUE, scoreParser.parsePass("Description. PASS TRUE"));
    }

    @Test
    void testParsePassShouldParseAStringWithUnexpectedSpaces() {
        assertEquals(EXPECTED_PASS_TRUE, scoreParser.parsePass("Description. PASS      true   "));
    }

    @Test
    void testParsePassShouldReturnAnEmptyOptionalIfTheKeywordIsNotInTheString() {
        assertEquals(EMPTY_PASS, scoreParser.parsePass("Description. The result is true! well done."));
    }

    @Test
    void testParseScoreShouldReturnEmptyIfTheResultCannotBeParsed() {
        assertEquals(EMPTY_PASS, scoreParser.parsePass("Description. PASS trube"));
    }
}
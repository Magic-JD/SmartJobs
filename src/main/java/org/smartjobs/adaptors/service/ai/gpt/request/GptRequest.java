package org.smartjobs.adaptors.service.ai.gpt.request;

import org.smartjobs.adaptors.service.ai.gpt.data.GptMessage;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;

import java.util.Arrays;
import java.util.List;

import static org.smartjobs.adaptors.service.ai.gpt.data.GptMessage.systemMessage;
import static org.smartjobs.adaptors.service.ai.gpt.data.GptMessage.userMessage;

public record GptRequest(GptModel model, double temperature, double topP, List<GptMessage> messages) {

    private static GptRequest gpt(GptMessage... messages) {
        return new GptRequest(GptModel.GPT_4_TURBO, 0.0, 0.1, Arrays.stream(messages).toList());
    }

    public static GptRequest extractCandidateName(String cvData) {
        return gpt(
                systemMessage("""
                        You are an expert on finding names in a CV document.
                        You will be able to scan the document and reliably and consistently find the name of the candidate from the document.
                        You will then return the name and only the name. You will not return any additional information.
                        Only return the candidates name. Return the candidates name with normal name casing.
                        If you can't find the name then answer with NAME_NOT_FOUND.
                        """),
                userMessage(cvData));
    }

    public static GptRequest anonymousCandidateDescription(String cvData) {
        return gpt(
                systemMessage("""
                        You are an expert in information extraction. You will be given the text from a candidates cv.
                        You will summarize the cv, keeping the candidates work history, certifications, skills, condensed versions of their role in previous jobs and their self description.
                        It is not important it is easy for a human to read. It only has to be easy for an ai model to read. Remove any unnecessary details.
                        For example "I led a highly skilled team of motivated individuals in optimizing the company websites latency by 32%" can be summarized as "Led team:Reduced latency by 32%"
                        Maintain length of employment and specific skills mentioned.
                        You will remove any details about the persons name, age, sex, sexual orientation or ethnicity. These details must not be present in the output.
                        If necessary replace details with a neutral replacement (e.g. replace Fraternity with Social Club or All Girls High School with High School).
                        Return only this information with no additional commentary.
                        This job is very important. Ensure you do it thoroughly. Output a maximum of 600 tokens, but if possible output less.
                        """),
                userMessage(cvData));
    }

    public static GptRequest scoreForCriteria(String cv, String criteria, int score) {
        return gpt(
                systemMessage(STR. """
                        You are the master of scoring candidates. You will be given a scoring criteria and a number of points.
                        Then you must carefully examine the cv that you are provided with.
                        As you examine that cv you must determine how well the candidate matches with the scoring criteria.
                        Output a maximum of two sentences explaining the reason why the candidate recieved the score you will give.
                        The reasoning should not include any reference to the number that you will award the candidate.
                        It must be focused on how well the candidate meets the given scoring criteria.
                        Then state the word SCORE
                        After that you must state the score.
                        For the score you must rate the candidate out of \{ score }.
                        The max amount you can give the candidate is \{ score }.
                        If the candidate doesn't match the scoring criteria at all they should get no points.
                        You may only award points for factors that are mentioned in the criteria.

                        Here are some examples of the correct output you should give:
                        Example 1: The candidate has held several positions where they have used Java, but shows no evidence of having used python. SCORE 6
                        Example 2: The candidate does not have an advanced degree in Computer Science. SCORE 0
                        Example 3: The candidate fully meets the supplied criteria. SCORE 10
                        """ ),
                userMessage(STR. "CV: \{ cv }. Scoring criteria: \{ criteria }" ));
    }


    public static GptRequest passForCriteria(String cv, String criteria) {
        return gpt(
                systemMessage(STR."""
                        You are the master of determining if candidates pass certain criteria.
                        You will be given a criteria for and determine if it is true or false that the client meets that criteria.
                        You must carefully examine the cv that you are provided with.
                        As you examine that cv you must determine how well the candidate matches with the criteria.
                        Output a maximum of two sentences explaining if it is true that the candidate met this criteria or if it is false.
                        It must be focused on how well the candidate meets the given criteria.
                        Then state the word PASS
                        After that you must state true if the candidate passed, otherwise false.
                        If the candidate doesn't match the scoring criteria at all then return false.

                        Here are some examples of the correct output you should give:
                        Example 1: The candidate does not have an advanced degree in Computer Science. PASS false
                        Example 2: The candidate has an aws certification. PASS true
                        """),
                userMessage(STR. "CV: \{ cv }. Scoring criteria: \{ criteria }" ));
    }
}
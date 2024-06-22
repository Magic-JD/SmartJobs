package org.smartjobs.adaptors.service.ai.gpt.request;

import org.smartjobs.adaptors.service.ai.gpt.data.GptMessage;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;

import java.util.Arrays;
import java.util.List;

import static org.smartjobs.adaptors.service.ai.gpt.data.GptMessage.systemMessage;
import static org.smartjobs.adaptors.service.ai.gpt.data.GptMessage.userMessage;

public record GptRequest(GptModel model, double temperature, double topP, List<GptMessage> messages) {

    private static GptRequest gpt(GptMessage... messages) {
        return new GptRequest(GptModel.GPT_4_O, 0.0, 0.1, Arrays.stream(messages).toList());
    }

    public static GptRequest extractCandidateName(String cvData) {
        return gpt(
                systemMessage("""
You are an expert in identifying names in CV documents.
Scan the document to reliably find and return only the candidate's name, with proper casing.
                        Do not include any additional information."""),
                userMessage(cvData));
    }

    public static GptRequest anonymousCandidateDescription(String cvData) {
        return gpt(
                systemMessage("""
You are an expert in information extraction.
Summarize the candidate's CV, including work history, certifications, skills, condensed job roles, and self-description.
Ensure the summary is easy for an AI model to read, not necessarily a human.
Remove unnecessary details and personal information such as name, age, sex, sexual orientation, and ethnicity, replacing them with neutral terms if needed.
Maintain the length of employment and specific skills mentioned.
Output only the summary, no additional commentary.
                                Limit the output to a maximum of 600 tokens."""
                ),
                userMessage(cvData));
    }

    public static GptRequest scoreForCriteria(String cv, String criteria, int score) {
        return gpt(
                systemMessage(STR. """
Master of scoring candidates.
Given scoring criteria and points, examine the provided CV.
Determine match with criteria.
Output max two sentences explaining the reason for the score, focused on the criteria match, without mentioning the score number.
State "SCORE" then the score out of \{score}.
Only award points for mentioned criteria factors.

Example output:

The candidate has held several positions where they have used Java, but shows no evidence of having used Python. SCORE 6
The candidate does not have an advanced degree in Computer Science. SCORE 0
The candidate fully meets the supplied criteria. SCORE 10"""
                ),
                userMessage(STR. "CV: \{ cv }. Scoring criteria: \{ criteria }" ));
    }


    public static GptRequest passForCriteria(String cv, String criteria) {
        return gpt(
                systemMessage(STR."""
Given criteria, determine if the candidate meets it (true/false). Examine the CV to assess criteria match.

Output two sentences max:

State if the candidate meets the criteria.
Follow with "PASS true/false".
If no match, return false.

Examples:

The candidate does not have an advanced degree in Computer Science. PASS false
                                The candidate has an AWS certification. PASS true"""
                ),
                userMessage(STR. "CV: \{ cv }. Scoring criteria: \{ criteria }" ));
    }
}
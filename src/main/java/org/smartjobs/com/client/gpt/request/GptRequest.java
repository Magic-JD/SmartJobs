package org.smartjobs.com.client.gpt.request;

import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.client.gpt.data.GptMessage;
import org.smartjobs.com.client.gpt.data.GptModel;
import org.smartjobs.com.service.candidate.data.ProcessedCv;

import java.util.Arrays;
import java.util.List;

import static org.smartjobs.com.client.gpt.data.GptMessage.systemMessage;
import static org.smartjobs.com.client.gpt.data.GptMessage.userMessage;

public record GptRequest(GptModel model, double temperature, double topP, List<GptMessage> messages) {

    private static GptRequest gpt3(GptMessage... messages) {
        return new GptRequest(GptModel.GPT_3_5, 0.0, 0.1, Arrays.stream(messages).toList());
    }

    private static GptRequest gpt4(GptMessage... messages) {
        return new GptRequest(GptModel.GPT_4_TURBO, 0.0, 0.1, Arrays.stream(messages).toList());
    }

    public static GptRequest extractCandidateName(String cvData) {
        return gpt3(
                systemMessage("""
                        You are an expert on finding names in a CV document.
                         You will be able to scan the document and reliably and consistently find the name of the candidate from the document.
                         You will then return the name and only the name. You will not return any additional information.
                         Only return the candidates name. If you can't find the name then please answer with NAME_NOT_FOUND.
                        """),
                userMessage(cvData));
    }

    public static GptRequest anonymousCandidateDescription(String cvData) {
        return gpt3(
                systemMessage("""
                        You are an expert in information extraction.
                        You will take a cv, and you will do two things.
                        You will summarize the rest of the cv in the most condensed way possible, maintaining the key information.
                        It is not important it is easy for a human to read. It only has to be easy for an ai model to read.
                        You will keep only the relevant information for determining the persons experience at a later date. Time periods of working should be kept. As should any specific skills or tools used.
                        It is important that you remove any details from the summary that reveal the persons name, age, sex, sexual orientation or ethnicity.
                        If they have information that is specific about these then replace them with a neutral replacement (e.g. replace Fraternity with Social Club or All Girls High School with High School)
                        Once you have extracted that information you will review it again and confirm that you have the highest quality answer to match this response.
                        Then return only this information with no additional commentary
                        """),
                userMessage(cvData));
    }

    public static GptRequest evaluateCandidate(String listingDescription, String candidateInformation) {
        return gpt3(
                systemMessage("""
                        You are an expert in evaluating candidates and finding which one is the best match for a job description.
                        I want you to consider the provided listing and the candidate, and then tell me a number of how well the candidate matches the job on a scale of 1 to 100.
                        Please consider your answer very carefully.
                        It is very important that you don't make a mistake as someones life is on the line.
                        However it is important that you are also critical and give an accurate score.
                        Only return the number, no other information.
                        Just return the number."""),
                userMessage("Job listing: " + listingDescription + " Candidate Information: " + candidateInformation));
    }


    public static GptRequest justifyGptDecision(int candidateRating, String candidateInformation, String jobListing) {
        return gpt3(
                systemMessage("""
                        You are a master at arguing your case.
                        You made a decision and you need to justify it as clearly as possible.
                        I will tell you the rating you gave the candidate as a percentage for the given job listing.
                        I will tell you the information about the candidate.
                        I will tell you the listing for the job.
                        You have to clearly and eloquently justify why you gave that candidate the rating that you did for being a match for this specific job."""),
                userMessage("Candidate rating: " + candidateRating + "%. Candidate Information: " + candidateInformation + " Job Listing: " + jobListing));
    }

    public static GptRequest scoreToCriteria(ProcessedCv cv, GptClient.ScoringCriteria scoringCriteria) {
        return gpt3(
                systemMessage(STR. """
                        You are the master of scoring candidates. You will be given a scoring criteria and a number of points.
                        Then you must carefully examine the cv that you are provided with.
                        As you examine that cv you must determine how well the candidate matches with the scoring criteria.
                        Your output must be as follows. First you must output a short, one sentence explaination for the role. After that you must state the score.
                        For the score you must rate the candidate out of \{ scoringCriteria.weight() }. The max amount you can give the candidate is \{ scoringCriteria.weight() }.
                        The output must be as follows.
                        First a short, single sentence explaining why the candidate recived this score.
                        DO NOT include information about the role, ONLY INCLUDE the reasoning for the score.
                        Then insert the word SCORE in upper case and finally the score itself. It should be only the score number, nothing additional.
                        """ ),
                userMessage(STR. "CV: \{ cv.condensedDescription() }. Scoring criteria: \{ scoringCriteria.description() }" ));
    }

    public static GptRequest scoreToCriteriaSingleRun(ProcessedCv cv) {
        return gpt3(
                systemMessage(STR."""
                        You are the master of scoring candidates. You will be given a scoring criteria.
                        The scoring criteria will ask you to consider a number of factors. You should make a reply that answers all of these factors.
                        The reply should be well thought out and highlight the score you are giving the candidate and why you are giving them the score.
                        As well as each of the individual scores, you should give them a total score which is the sum of all their previous scores at the end.
                        """),
                userMessage(STR. """
                CV: \{ cv.condensedDescription() }.

Scoring criteria:
1. Education (Total: 20 Points)
Degree in Computer Science or related field (10 points)
Advanced degree (MSc, Ph.D.) in a relevant field (additional 5 points)
Relevant certifications (e.g., AWS Certified Developer, Microsoft Certified: Azure Developer Associate) (up to 5 points)
2. Technical Skills (Total: 30 Points)
Proficiency in Java and Python (up to 10 points)
Likely to be familiar with React (or Angular, Vue.js) (up to 10 points)
Understanding of databases and cloud platforms (up to 10 points)
3. Professional Experience (Total: 30 Points)
Relevant consumer tech industry experience (1 point per year, up to 10 points)
Experience in a Software Engineering role (5 points)
Demonstrated leadership and/or coaching role (5 points)
Notable projects or contributions (up to 10 points for complexity and relevance)
4. Achievements and Portfolio (Total: 10 Points)
Published work (papers, patents) in the field (up to 5 points)
Contributions to open-source projects or public code repositories (e.g., GitHub) (up to 5 points)
5. Soft Skills and Cultural Fit (Total: 10 Points)
Evidence of teamwork, leadership, or other soft skills (up to 5 points)
Alignment with agile methodologies (5 points)
                """ ));
    }
}
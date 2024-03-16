package org.smartjobs.com.client.gpt.request;

import org.smartjobs.com.client.gpt.data.GptMessage;
import org.smartjobs.com.client.gpt.data.GptModel;

import java.util.Arrays;
import java.util.List;

import static org.smartjobs.com.client.gpt.data.GptMessage.systemMessage;
import static org.smartjobs.com.client.gpt.data.GptMessage.userMessage;

public record GptRequest(GptModel model, List<GptMessage> messages) {
    private static GptRequest gpt3(GptMessage... messages) {
        return new GptRequest(GptModel.GPT_3_5, Arrays.stream(messages).toList());
    }

    public static GptRequest informationExtractionRequest(String cvData) {
        return gpt3(
                systemMessage("""
                        You are an expert in information extraction that only returns answers in a json format.
                        You will take a cv, and you will do two things.
                        First you will determine what the name of the applicant is.
                        You can deduce from the text what the applicants name is.
                        Then you will summarize the rest of the cv in the most condensed way possible, maintaining the key information.
                        It is not important it is easy for a human to read. It only has to be easy for an ai model to read.
                        You will keep only the relevant information for determining the persons experience at a later date. Time periods of working should be kept. As should any specific skills or tools used.
                        It is important that you remove any details from the summary that reveal the persons name, age, sex or ethnicity.
                        Once you have extracted that information you will review it again and confirm that you have the highest quality answer to match this response.
                        Then you will return the answer in this json format (using proper quotations)
                        {name : |THE NAME YOU EXTRACTED|, description : |THE DESCRIPTION YOU EXTRACTED|}.
                        After you have verified this is valid json please return it."""),
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

}
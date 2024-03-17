package org.smartjobs.com.controller.htmx;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.smartjobs.com.controller.candidate.CandidateController;
import org.smartjobs.com.controller.listing.ListingController;
import org.smartjobs.com.controller.listing.request.MatchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/htmx")
public class HtmxController {

    private final CandidateController candidateController;
    private final ListingController listingController;

    @Autowired
    public HtmxController(ListingController listingController, CandidateController candidateController) {
        this.candidateController = candidateController;
        this.listingController = listingController;
    }

    @HxRequest
    @PostMapping("/candidate/upload")
    public String uploadFile(
            @RequestParam(name = "files") MultipartFile[] files
    ) {
        HttpStatus httpStatus = candidateController.uploadFile(files);
        String html = """
                    <h1 id="uploadStatus">Uploaded - Please provide listing</h1>
                    <br>
                    <div>
                        <textarea id="listing"
                                  name="listing"
                                  rows="4"
                                  cols="50"></textarea><br>
                        <button type="button"
                                hx-post="http://localhost:8080/htmx/listing/match"
                                hx-include="#listing"
                                hx-trigger="click"
                                hx-target="#mainBody"
                                hx-swap"innerHTML">Send</button>
                    </div>
                    <div id="responseDiv"></div>
                """;
        return switch (httpStatus) {
            case OK -> html;
            default -> "There was an issue with uploading the files";
        };
    }

    @HxRequest
    @PostMapping("/listing/match")
    @ResponseBody
    public String evaluateCandidate(@RequestParam String listing) {
        String justification = listingController.evaluateCandidate(new MatchRequest(listing)).getBody().justification();
        return STR. """
        <div>
        <div id="speech-bubble-gpt">\{ justification }</div>
        <textarea id="ask-futher" name="ask-further" rows="4" cols="50"></textarea>
        </div>
        """ ;
    }
}

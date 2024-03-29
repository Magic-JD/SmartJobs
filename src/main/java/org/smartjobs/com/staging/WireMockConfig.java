package org.smartjobs.com.staging;

import org.springframework.context.annotation.Configuration;


@Configuration
public class WireMockConfig {

//    @Bean
//    public WireMockServer wiremock(
//            @Value("${gpt.api.site}") String site,
//            @Value("${gpt.api.endpoint}") String endpoint,
//            @Autowired Gson gson) {
//        WireMockServer wireMockServer = new WireMockServer();
//        wireMockServer.start();
//        String[] split = site.split(":");
//        configureFor(split[0], Integer.parseInt(split[1]));
//        stubFor(
//                post(
//                        urlEqualTo(endpoint))
//                        .willReturn(aResponse()
//                                .withBody(
//                                        gson.toJson(
//                                                new GptResponse("", "", 11111111, GptModel.GPT_3_5, new GptUsage(
//                                                        100, 200, 300
//                                                ), List.of(new GptChoices(
//                                                        new GptMessage(
//                                                                GptRole.SYSTEM, "THIS IS TEST CONTENT SCORE 5"
//                                                        ), "", "", 0)
//                                                )
//                                                )
//                                        )
//                                )
//                        )
//        );
//        return wireMockServer;
//    }

}

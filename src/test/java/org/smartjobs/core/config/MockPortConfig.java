package org.smartjobs.core.config;

import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.*;
import org.smartjobs.core.ports.listener.Listener;
import org.smartjobs.core.service.event.events.Event;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static constants.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockPortConfig {


    @Bean
    @Primary
    public AiService aiService() {
        return aiServiceMock();
    }

    @Bean
    @Primary
    public AnalysisDal analysisDal() {
        return analysisDalMock();
    }

    @Bean
    @Primary
    public CredentialDal credentialDal() {
        return credentialDalMock();
    }

    @Bean
    @Primary
    public CreditDal creditDal() {
        return creditDalMock();
    }

    @Bean
    @Primary
    public CvDal cvDal() {
        return cvDalMock();
    }

    @Bean
    @Primary
    public DefinedScoringCriteriaDal definedScoringCriteriaDal() {
        return definedScoringCriteriaDalMock();
    }


    @Bean
    @Primary
    public RoleDal roleDal() {
        return roleDalMock();
    }

    @Bean
    @Primary
    public Listener listener() {
        Listener listener = mock(Listener.class);
        doNothing().when(listener).processEvent(any(Event.class));
        return listener;
    }
}

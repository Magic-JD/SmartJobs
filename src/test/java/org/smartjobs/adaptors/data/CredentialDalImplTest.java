package org.smartjobs.adaptors.data;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.CredentialRepository;
import org.smartjobs.adaptors.data.repository.data.Credential;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.ports.dal.CredentialDal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CredentialDalImplTest {

    private final CredentialRepository credentialRepository = mock(CredentialRepository.class);
    private CredentialDal credentialDal = new CredentialDalImpl(credentialRepository);

    private final ArgumentCaptor<Credential> credentialArgumentCaptor = ArgumentCaptor.forClass(Credential.class);

    @Test
    void testThatGetUserGetsUserWithCorrectValues() {
        when(credentialRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new Credential(USER_ID, USERNAME, PASSWORD)));
        Optional<User> userOptional = credentialDal.getUser(USERNAME);
        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(USER_ID, user.getId());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("USER", authorities.stream().findFirst().orElseThrow().getAuthority());
    }

    @Test
    void testThatCreateUserWillCreateAUserWithTheRightSettings() {
        boolean set = credentialDal.setUser(USERNAME, PASSWORD);
        assertTrue(set);
        verify(credentialRepository).save(credentialArgumentCaptor.capture());
        Credential credential = credentialArgumentCaptor.getValue();
        assertEquals(USERNAME, credential.getUsername());
        assertEquals(PASSWORD, credential.getPassword());
    }

}

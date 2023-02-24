package org.eclipse.tractusx.ssi.spi.verifiable.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

public class VerifiableCredentialDeserializationTest {

    @Test
    @SneakyThrows
    public void VerifiableCredentialTestSuccess(){
        // given
        String testVc = getTestCredential("credentials/01_validVCFull.json");
        ObjectMapper om = new ObjectMapper();
        // when
        AtomicReference<VerifiableCredential> ar = null;
        VerifiableCredential result = om.readValue(testVc, VerifiableCredential.class);
        // then
        Assertions.assertFalse(Strings.isNullOrEmpty(result.getId().toString()));
        Assertions.assertFalse(result.getTypes().isEmpty());
        Assertions.assertNotEquals(result.getIssuer(), new URI(""));
        Assertions.assertNotNull(result.getExpirationDate());
        Assertions.assertNotNull(result.getCredentialStatus().getId());
        Assertions.assertNotNull(result.getCredentialStatus().getType());
        Assertions.assertNotNull(result.getProof().getProofValue());
        Assertions.assertNotNull(result.credentialSubject);
    }

    @Test
    @SneakyThrows
    public void VerifiableCredentialTestFail(){
        String testVc = getTestCredential("credentials/02_invalidVCWithMissingId.json");
        ObjectMapper om = new ObjectMapper();

        Assertions.assertThrows(ValueInstantiationException.class,
                () -> om.readValue(testVc, VerifiableCredential.class));
    }

    @SneakyThrows
    public String getTestCredential(String path) {
        var classLoader = getClass().getClassLoader();
        return new String(classLoader.getResourceAsStream(path).readAllBytes());
    }
}

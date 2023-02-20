package org.eclipse.tractusx.ssi.spi.verifiable.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
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
        String testVc = getTestCredential("spi/verifiable/credential/01_validVCFull.json");
        ObjectMapper om = new ObjectMapper();
        // when
        AtomicReference<VerifiableCredential> ar = null;
        VerifiableCredential result = om.readValue(testVc, VerifiableCredential.class);
        // then
        Assertions.assertFalse(result.getId().equals(new String()));
        Assertions.assertFalse(result.getTypes().isEmpty());
        Assertions.assertFalse(result.getIssuer().equals(new URI("")));
        Assertions.assertTrue(result.getExpirationDate() != null);
        Assertions.assertTrue(result.getCredentialStatus().getId() != null);
        Assertions.assertTrue(result.getCredentialStatus().getType() != null);
        Assertions.assertTrue(result.getProof().getProofValue() != null);
        Assertions.assertNotNull(result.credentialSubject);
    }

    @Test
    @SneakyThrows
    public void VerifiableCredentialTestFail(){
        // given
        String testVc = getTestCredential("spi/verifiable/credential/02_invalidVCWithMissingDID.json");
        ObjectMapper om = new ObjectMapper();
        String expectedMessage = "ValueInstantiationException";
        // when
        ValueInstantiationException exception = Assertions.assertThrows(ValueInstantiationException.class,
                () -> om.readValue(testVc, VerifiableCredential.class));
        // then
        Assertions.assertTrue(exception.toString().contains(expectedMessage));
    }

    @SneakyThrows
    public String getTestCredential(String path) {
        var classLoader = getClass().getClassLoader();
        return new String(classLoader.getResourceAsStream(path).readAllBytes());
    }
}

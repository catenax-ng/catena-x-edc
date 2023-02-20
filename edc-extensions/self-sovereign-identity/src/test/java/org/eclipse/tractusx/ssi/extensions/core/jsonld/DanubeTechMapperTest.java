package org.eclipse.tractusx.ssi.extensions.core.jsonld;

import org.eclipse.tractusx.ssi.extensions.core.exception.SsiException;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.spi.verifiable.presentation.VerifiablePresentation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DanubeTechMapperTest {

    DanubTechMapper mapper;

    /**
     * DanubTech Verifiable Presentation to Verifiable Credential and vice versa
     */
    @Test
    public void mapVerifiableCredentialDtPresSuccess() {
        // given
        com.danubetech.verifiablecredentials.VerifiablePresentation dtVp;
        dtVp = DanubCredentialFactory.getTestDanubVP();
        VerifiablePresentation toTest = mapper.map(dtVp);
        // when
        com.danubetech.verifiablecredentials.VerifiablePresentation result = mapper.map(toTest);
        //then
        Assertions.assertEquals(result.getHolder(), dtVp.getHolder());
        Assertions.assertEquals(result.getTypes(), dtVp.getTypes());
        Assertions.assertEquals(result.getId(), dtVp.getId());
        Assertions.assertEquals(result.getLdProof(), dtVp.getLdProof());
        Assertions.assertEquals(result.getVerifiableCredential().getId(), dtVp.getVerifiableCredential().getId());

    }

    @Test
    public void mapVerifiableCredentialToDtPresFail() {
        Assertions.assertThrows(NullPointerException.class,
                () -> mapper.map((VerifiablePresentation) null));
    }

    @Test
    public void mapDtPresToVerifiablePresentationSuccess() {
        // given
        com.danubetech.verifiablecredentials.VerifiablePresentation toTest;
        toTest = DanubCredentialFactory.getTestDanubVP();
        // when
        VerifiablePresentation result = mapper.map(toTest);
        //then
        Assertions.assertEquals(result.getHolder(), toTest.getHolder());
        Assertions.assertEquals(result.getTypes(), toTest.getTypes());
        Assertions.assertEquals(result.getId(), toTest.getId());
        Assertions.assertEquals(result.getProof(), toTest.getLdProof());
        Assertions.assertEquals(result.getVerifiableCredentials().get(0).getId(), toTest.getVerifiableCredential().getId());
    }

    @Test
    public void mapDtPresToVerifiableCredentialFail() {
        // given
        com.danubetech.verifiablecredentials.VerifiablePresentation toTest;
        toTest = DanubCredentialFactory.getInvalidTestDanubVP();
        String expectedMessage = "java.lang.NullPointerException";
        // when
        NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                () -> mapper.map(toTest));
        // then
        Assertions.assertTrue(exception.toString().equals(expectedMessage));
    }

    /**
     * DanubTech to Verifiable Credential and vice versa
     */
    @Test
    public void mapDtCredToVerifiableCredentialSuccess() {
        // given
        com.danubetech.verifiablecredentials.VerifiableCredential toTest;
        toTest = DanubCredentialFactory.getTestDanubVC();
        // when
        org.eclipse.tractusx.ssi.spi.verifiable.credential.VerifiableCredential result = mapper.map(toTest);
        //then
        Assertions.assertEquals(result.getId(), toTest.getId());
        Assertions.assertEquals(result.getTypes(), toTest.getTypes());
        Assertions.assertEquals(result.getProof(), toTest.getLdProof());
        Assertions.assertEquals(result.getExpirationDate(), toTest.getExpirationDate().toInstant());
        //Assertions.assertEquals(result.getClaims(), toTest.getCredentialSubject().getClaims()); // TODO Check subject
    }

    @Test
    public void mapDtCredToVerifiableCredentialFail() {
        // given
        com.danubetech.verifiablecredentials.VerifiableCredential toTest;
        toTest = DanubCredentialFactory.getInvalidTestDanubVC();
        // when
        Assertions.assertThrows(SsiException.class, () -> mapper.map(toTest));
    }

    @Test
    public void mapVerifiableCredentialToDtCredSuccess() {
        // given
        com.danubetech.verifiablecredentials.VerifiableCredential dtVc;
        dtVc = DanubCredentialFactory.getTestDanubVC();
        VerifiableCredential toTest = mapper.map(dtVc);
        // when
        com.danubetech.verifiablecredentials.VerifiableCredential result = mapper.map(toTest);
        //then
        Assertions.assertEquals(toTest.getId(), result.getId());
        Assertions.assertEquals(toTest.getTypes(), result.getTypes());
        Assertions.assertEquals(toTest.getProof(), result.getLdProof());
        Assertions.assertEquals(toTest.getExpirationDate(), result.getExpirationDate().toInstant());
    }

    @Test
    public void mapVerifiableCredentialDtCredFail() {
        // given
        VerifiableCredential toTest = null;
        Assertions.assertThrows(NullPointerException.class, () -> mapper.map(toTest));
    }
}

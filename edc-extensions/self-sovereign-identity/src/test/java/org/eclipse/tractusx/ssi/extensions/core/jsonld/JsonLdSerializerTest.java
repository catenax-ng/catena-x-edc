package org.eclipse.tractusx.ssi.extensions.core.jsonld;

import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.extensions.core.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.spi.verifiable.presentation.VerifiablePresentation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonLdSerializerTest {

  DanubCredentialFactory credentialFactory;
  SerializedVerifiablePresentation svp;
  JsonLdSerializer jsonLdSerializer;

  @BeforeEach
  public void setUp() {
    jsonLdSerializer = new JsonLdSerializerImpl();
  }

  @Test
  public void serializePresentationTestSuccess() {
    // given
    com.danubetech.verifiablecredentials.VerifiablePresentation vpDt =
        credentialFactory.getTestDanubVP();
    svp = new SerializedVerifiablePresentation(vpDt.toJson());
    // when
    VerifiablePresentation toTest = jsonLdSerializer.deserializePresentation(svp);
    SerializedVerifiablePresentation serializePresentation =
        jsonLdSerializer.serializePresentation(toTest);
    VerifiablePresentation result = jsonLdSerializer.deserializePresentation(serializePresentation);
    // then
    Assertions.assertEquals(toTest, result);
  }

  @Test
  public void serializePresentationFailTest() {
    Assertions.assertThrows(
        NullPointerException.class, () -> jsonLdSerializer.serializePresentation(null));
  }

  @Test
  public void deserializePresentationTestSuccess() {
    // given
    VerifiablePresentation toTest = DanubTechMapper.map(credentialFactory.getTestDanubVP());
    // when
    SerializedVerifiablePresentation serializePresentation =
        jsonLdSerializer.serializePresentation(toTest);
    VerifiablePresentation result = jsonLdSerializer.deserializePresentation(serializePresentation);
    // then
    Assertions.assertEquals(toTest, result);
  }

  @Test
  public void deserializePresentationFailTest() {
    Assertions.assertThrows(
        NullPointerException.class, () -> jsonLdSerializer.deserializePresentation(null));
  }
}

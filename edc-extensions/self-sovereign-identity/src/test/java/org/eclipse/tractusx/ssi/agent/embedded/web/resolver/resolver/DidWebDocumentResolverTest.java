package org.eclipse.tractusx.ssi.agent.embedded.web.resolver.resolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import lombok.SneakyThrows;
import okhttp3.*;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.Did;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.spi.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.web.resolver.DidWebDocumentResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DidWebDocumentResolverTest {

  private static final String DID_JSON =
      "{"
          + "   \"@context\": \"https://www.w3.org/ns/did/v1\","
          + "   \"id\": \"did:web:example.com\","
          + "   \"verificationMethod\": ["
          + "       {"
          + "           \"id\": \"did:web:example.com\","
          + "           \"type\": \"Ed25519VerificationKey2020\","
          + "           \"controller\": \"did:web:example.com\","
          + "           \"publicKeyMultibase\": \"zEYJrMxWigf9boyeJMTRN4Ern8DJMoCXaLK77pzQmxVjf\""
          + "       }"
          + "   ],"
          + "   \"authentication\": ["
          + "       \"did:web:example.com\""
          + "   ]"
          + "}";

  private DidWebDocumentResolver resolver;

  // mocks
  private OkHttpClient httpClient;
  private Monitor monitor;

  @BeforeEach
  public void setUp() {
    httpClient = Mockito.mock(OkHttpClient.class);
    monitor = Mockito.mock(Monitor.class);
    resolver = new DidWebDocumentResolver(httpClient, monitor);
  }

  @Test
  @SneakyThrows
  public void resolveDidWebDocumentSuccess() {
    // given
    Response responseMock = Mockito.mock(Response.class);
    Call callMock = Mockito.mock(Call.class);
    ResponseBody responseBodyMock = Mockito.mock(ResponseBody.class);
    doReturn(callMock).when(httpClient).newCall(any(Request.class));
    doReturn(responseMock).when(callMock).execute();
    doReturn(true).when(responseMock).isSuccessful();
    doReturn(responseBodyMock).when(responseMock).body();
    doReturn(DID_JSON.getBytes()).when(responseBodyMock).bytes();

    // when
    Did randomDid = new Did(new DidMethod("web"), new DidMethodIdentifier("someurl.com"));
    DidDocument result = resolver.resolve(randomDid);

    // then
    Assertions.assertTrue(result.getId() != null);
    Assertions.assertTrue(result.getVerificationMethods() != null);
    Assertions.assertTrue(result.getVerificationMethods().size() == 1);
  }
}

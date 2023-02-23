package org.eclipse.tractusx.ssi.extensions.did.web.resolver;

import lombok.SneakyThrows;
import okhttp3.*;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.spi.did.DidMethodIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class DidWebDocumentResolverTest {

  private DidWebDocumentResolver resolver;
  private OkHttpClient clientMock;

  @BeforeEach
  public void setUp(){
    clientMock = Mockito.mock(OkHttpClient.class);
    resolver = new DidWebDocumentResolver(clientMock);
  }


  @Test
  @SneakyThrows
  public void resolveDidWebDocumentSuccess(){
    // given
    Did toTest = new Did(new DidMethod("web"), new DidMethodIdentifier("someurl.com"));
    String testDidDocument = getTestDidDocument();
    Response responseMock = Mockito.mock(Response.class);
    Call callMock = Mockito.mock(Call.class);
    ResponseBody responseBodyMock = Mockito.mock(ResponseBody.class);
    doReturn(callMock).when(clientMock).newCall(any(Request.class));
    doReturn(responseMock).when(callMock).execute();
    doReturn(true).when(responseMock).isSuccessful();
    doReturn(responseBodyMock).when(responseMock).body();
    doReturn(testDidDocument.getBytes()).when(responseBodyMock).bytes();
    // when
    DidDocument result = resolver.resolve(toTest);
    // then
    Assertions.assertTrue(result.getId() != null);
    Assertions.assertTrue(result.getVerificationMethods() != null);
    Assertions.assertTrue(result.getVerificationMethods().size() == 1);
  }

  @SneakyThrows
  private String getTestDidDocument(){
    InputStream in = DidWebDocumentResolverTest.class.getClassLoader().getResourceAsStream("did/webdid.json");
    String didDocumentString = new String(in.readAllBytes());
    return didDocumentString;
  }
}

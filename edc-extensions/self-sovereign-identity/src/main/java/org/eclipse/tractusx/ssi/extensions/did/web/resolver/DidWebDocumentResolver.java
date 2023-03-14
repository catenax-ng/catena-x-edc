package org.eclipse.tractusx.ssi.extensions.did.web.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.extensions.core.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.extensions.core.exception.SsiException;
import org.eclipse.tractusx.ssi.extensions.did.web.exception.DidWebException;
import org.eclipse.tractusx.ssi.extensions.did.web.util.Constants;
import org.eclipse.tractusx.ssi.extensions.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.eclipse.tractusx.ssi.spi.did.DidDocument;
import org.eclipse.tractusx.ssi.spi.did.DidMethod;
import org.eclipse.tractusx.ssi.spi.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.spi.did.resolver.DidDocumentResolver;

public class DidWebDocumentResolver implements DidDocumentResolver {

  private final OkHttpClient client;
  private final Monitor monitor;

  public DidWebDocumentResolver(OkHttpClient client, Monitor monitor) {
    this.client = client;
    this.monitor = monitor;
  }

  @Override
  public DidMethod getSupportedMethod() {
    return Constants.DID_WEB_METHOD;
  }

  @Override
  public DidDocument resolve(Did did) {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD))
      throw new SsiException(
          "Handler can only handle the following methods:" + Constants.DID_WEB_METHOD);

    final URL url = DidWebParser.parse(did);

    final Request request = new Request.Builder().get().url(url).build();

    try (final Response response = client.newCall(request).execute()) {

      if (!response.isSuccessful()) {
        throw new DidWebException(response.message());
      }
      if (response.body() == null) {
        throw new DidWebException("Empty response body");
      }

      final byte[] body = response.body().bytes();

      // TODO Fix this
      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode didNode = mapper.readTree(body);

      final String id = didNode.get("id").asText();
      final JsonNode verificationMethodNode = didNode.get("verificationMethod");

      final List<Ed25519VerificationKey2020> keys = new ArrayList<>();
      if (verificationMethodNode.isArray()) {
        verificationMethodNode
            .elements()
            .forEachRemaining(
                jsonNode -> {
                  var key = parseKeyNode(did, jsonNode);
                  keys.add(key);
                });
      } else {
        Ed25519VerificationKey2020 key = parseKeyNode(did, verificationMethodNode);
        keys.add(key);
      }

      return DidDocument.builder().id(URI.create(id)).verificationMethods(keys).build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Ed25519VerificationKey2020 parseKeyNode(Did did, JsonNode verificationMethodNode) {
    final String verificationMethodType = verificationMethodNode.get("type").asText();
    final String verificationMethodId = verificationMethodNode.get("id").asText();
    final String verificationMethodController = verificationMethodNode.get("controller").asText();
    final String verificationMethodKey = verificationMethodNode.get("publicKeyMultibase").asText();

    if (!Objects.equals(verificationMethodType, Ed25519VerificationKey2020.TYPE)) {
      monitor.warning(
          String.format(
              "Skipped unsupported verification key type in DID '%s'. Supported Types: [%s]",
              did, Ed25519VerificationKey2020.TYPE));
    }

    var key =
        Ed25519VerificationKey2020.builder()
            .id(URI.create(verificationMethodId))
            .controller(URI.create(verificationMethodController))
            .multibase(MultibaseFactory.create(verificationMethodKey))
            .build();
    return key;
  }
}

package org.eclipse.tractusx.ssi.extensions.did.web.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    public DidWebDocumentResolver(OkHttpClient client) {
        this.client = client;
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

            final org.eclipse.tractusx.ssi.extensions.did.web.resolver.DidDocument resolvedDocument =
                    new ObjectMapper()
                            .readValue(
                                    body, org.eclipse.tractusx.ssi.extensions.did.web.resolver.DidDocument.class);

            final List<Ed25519VerificationKey2020> keys =
                    resolvedDocument.getVerificationMethods().stream()
                            .filter(k -> k.getDidVerificationMethodType().equals(Ed25519VerificationKey2020.TYPE))
                            .map(this::mapKey)
                            .collect(Collectors.toList());

            return DidDocument.builder().id(resolvedDocument.getId()).verificationMethods(keys).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Ed25519VerificationKey2020 mapKey(Ed25519VerificationKey2020 key) {
        return Ed25519VerificationKey2020.builder()
                .id(key.getId())
                .controller(key.getController())
                .publicKeyMultibase(key.getPublicKeyMultibase())
                .build();
    }
}

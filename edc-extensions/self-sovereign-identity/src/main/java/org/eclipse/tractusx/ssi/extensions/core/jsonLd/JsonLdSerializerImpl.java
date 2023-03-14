package org.eclipse.tractusx.ssi.extensions.core.jsonLd;

import org.eclipse.tractusx.ssi.extensions.core.credentials.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.spi.verifiable.presentation.VerifiablePresentation;

public class JsonLdSerializerImpl implements JsonLdSerializer {

  @Override
  public SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation) {

    final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation =
        DanubTechMapper.map(verifiablePresentation);
    final String dtPresentationJson = dtPresentation.toJson();

    return new SerializedVerifiablePresentation(dtPresentationJson);
  }

  @Override
  public VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation) {

    final String serializedPresentationJson = serializedPresentation.getJson();
    final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation =
        com.danubetech.verifiablecredentials.VerifiablePresentation.fromJson(
            serializedPresentationJson);

    return DanubTechMapper.map(dtPresentation);
  }
}

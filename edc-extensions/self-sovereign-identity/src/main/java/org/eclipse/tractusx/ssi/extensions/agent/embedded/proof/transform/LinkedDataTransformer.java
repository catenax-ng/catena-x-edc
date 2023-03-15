package org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.transform;

import foundation.identity.jsonld.JsonLDException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd.DanubTechMapper;
import org.eclipse.tractusx.ssi.extensions.core.spi.verifiable.credential.VerifiableCredential;

public class LinkedDataTransformer {
  public TransformedLinkedData transform(VerifiableCredential credential) {
    var dtCredential = DanubTechMapper.map(credential);
    try {

      var normalized = dtCredential.normalize("urdna2015");
      return new TransformedLinkedData(normalized);

    } catch (JsonLDException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

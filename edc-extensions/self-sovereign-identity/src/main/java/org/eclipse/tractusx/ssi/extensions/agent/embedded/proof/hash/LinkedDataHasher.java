package org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.extensions.agent.embedded.proof.transform.TransformedLinkedData;

public class LinkedDataHasher {

  public HashedLinkedData hash(TransformedLinkedData transformedLinkedData) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      var value = digest.digest(transformedLinkedData.getValue().getBytes());
      return new HashedLinkedData(value);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}

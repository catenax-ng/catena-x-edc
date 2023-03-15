package org.eclipse.tractusx.ssi.extensions.agent.embedded.jsonLd;

import foundation.identity.jsonld.JsonLDObject;

public interface JsonLdValidator {

  public boolean validate(JsonLDObject jsonLdObject);
}

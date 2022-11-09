package org.eclipse.tractusx.edc.tests.data;

import java.util.Map;
import lombok.NonNull;
import lombok.Value;

@Value
public class DataAddress {

  @NonNull Map<String, Object> properties;
}

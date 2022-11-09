package org.eclipse.tractusx.edc.tests.data;

import lombok.NonNull;
import lombok.Value;

@Value
public class TransferProcess {
  @NonNull String id;
  @NonNull TransferProcessState state;
}

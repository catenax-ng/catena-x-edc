package org.eclipse.tractusx.ssi.spi.did;


import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode
public class DidMethodIdentifier {
    @NonNull String value;
}
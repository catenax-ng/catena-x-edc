package net.catenax.edc.tests.data;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class Policy {
  @NonNull private String id;
  @NonNull private List<Permission> Permission;
}

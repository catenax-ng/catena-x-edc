package net.catenax.edc.tests;

import static net.catenax.edc.tests.Constants.DATA_MANAGEMENT_URL;
import static net.catenax.edc.tests.Constants.DATA_PLANE_URL;
import static net.catenax.edc.tests.Constants.IDS_URL;
import static net.catenax.edc.tests.Constants.PLATO;
import static net.catenax.edc.tests.Constants.SOKRATES;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(access = AccessLevel.PRIVATE)
@Getter
class Environment {
  @NonNull private final String dataManagementUrl;
  @NonNull private final String idsUrl;
  @NonNull private final String dataPlaneUrl;

  public static Environment plato() {
    return byName(PLATO);
  }

  public static Environment sokrates() {
    return byName(SOKRATES);
  }

  public static Environment byName(String name) {
    name = name.toUpperCase(Locale.ROOT);

    return Environment.builder()
        .dataManagementUrl(System.getenv(String.join("_", name, DATA_MANAGEMENT_URL)))
        .idsUrl(System.getenv(String.join("_", name, IDS_URL)))
        .dataPlaneUrl(System.getenv(String.join("_", name, DATA_PLANE_URL)))
        .build();
  }
}

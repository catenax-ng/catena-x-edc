package net.catenax.edc.business.tests;

public class DescriptionFactory {

  public ControlPlaneDescription describeControlPlaneA() {
    String dataMgmtUrl = System.getenv(EnvironmentVariables.CONNECTOR_A_DATA_MGMT_URL);
    if (dataMgmtUrl == null || dataMgmtUrl.isEmpty()) {
      throw new RuntimeException(
          String.format(
              "required environment variable not set %s",
              EnvironmentVariables.CONNECTOR_A_DATA_MGMT_URL));
    }

    return new ControlPlaneDescription(dataMgmtUrl);
  }
}

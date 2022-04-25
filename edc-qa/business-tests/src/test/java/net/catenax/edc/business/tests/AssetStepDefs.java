package net.catenax.edc.business.tests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;

public class AssetStepDefs {

  private final DescriptionFactory descriptionFactory = new DescriptionFactory();
  private final Map<String, Asset> assetByIdentifier = new HashMap<>();
  private String lastAssetIdentifier;
  private Response lastRetrievedResponse;

  @Given("asset {string}")
  public void newAsset(String identifier) {
    lastAssetIdentifier = identifier;
    assetByIdentifier.put(identifier, new Asset());
  }

  @Given("has asset description {string}")
  public void hasDescription(String description) {
    assetByIdentifier.get(lastAssetIdentifier).setDescription(description);
  }

  @When("asset {string} is created for Connector {string}")
  public void createAsset(String assetIdentifier, String connectorIdentifier) {
    ControlPlaneDescription controlPlaneDescription;
    if (Objects.equals(connectorIdentifier, "A")) {
      controlPlaneDescription = descriptionFactory.describeControlPlaneA();
    } else {
      throw new RuntimeException(
          String.format("Unsupported connector identifier %s", connectorIdentifier));
    }

    final Asset asset = assetByIdentifier.get(assetIdentifier);
    final String dataMgmtUrl = controlPlaneDescription.getDataManagementUrl();
    final String requestBody =
        String.format(
            "{"
                + "   \"asset\": {"
                + "       \"properties\": {"
                + "           \"asset:prop:id\": \"%s\","
                + "           \"asset:prop:description\": \"%s\""
                + "       }"
                + "   },"
                + "   \"dataAddress\": {"
                + "       \"properties\": {"
                + "           \"type\": \"HttpProxy\","
                + "           \"endpoint\": \"https://example.com\""
                + "       }"
                + "  }"
                + "}",
            asset.getId(), asset.getDescription());

    RestAssured.baseURI = dataMgmtUrl;

    lastRetrievedResponse =
        RestAssured.given()
            .header("Content-Type", "application/json")
            .and()
            .body(requestBody)
            .when()
            .post("/assets")
            .then()
            .statusCode(HttpStatus.SC_NO_CONTENT)
            .extract()
            .response();
  }

  @Then("Connector {string} has asset {string}")
  public void assertAsset(String connectorIdentifier, String assetIdentifier) {
    ControlPlaneDescription controlPlaneDescription;
    if (Objects.equals(connectorIdentifier, "A")) {
      controlPlaneDescription = descriptionFactory.describeControlPlaneA();
    } else {
      throw new RuntimeException(
          String.format("Unsupported connector identifier %s", connectorIdentifier));
    }

    final Asset asset = assetByIdentifier.get(assetIdentifier);
    final String dataManagementUrl = controlPlaneDescription.getDataManagementUrl();

    RestAssured.baseURI = dataManagementUrl;
    lastRetrievedResponse =
        RestAssured.given()
            .header("Content-type", "application/json")
            .when()
            .get(dataManagementUrl + "/assets/" + asset.getId())
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response();
  }

  @Given("with asset description {string}")
  public void withDescription(String description) {
    final Map<String, String> properties = lastRetrievedResponse.jsonPath().getMap("properties");
    Assertions.assertEquals(description, properties.get("asset:prop:description"));
  }

  private static class Asset {

    public Asset() {
      /* generate ID randomly to have tests idempotent */
      id = UUID.randomUUID().toString();
    }

    @Getter private final String id;

    @Getter @Setter private String description;
  }
}

package org.eclipse.tractusx.ssi.extensions.core.jwt;

import static org.mockito.Mockito.doReturn;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.extensions.core.setting.SsiSettings;
import org.eclipse.tractusx.ssi.spi.did.Did;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SignedJwtValidatorTest {

  SsiSettings ssiSettings;
  SignedJWT jwt;

  SignedJwtValidator signedJwtValidator;

  @BeforeEach
  public void init() {
    ssiSettings = Mockito.mock(SsiSettings.class);
    jwt = Mockito.mock(SignedJWT.class);
  }

  @Test
  @SneakyThrows
  public void validateSuccess() {
    // given
    List<String> audiences = Arrays.asList("WrongAudience", "CorrectAudience");
    Date futureDate = getFutureDate();
    Did didMock = Mockito.mock(Did.class);
    JWTClaimsSet claimsSetMock = Mockito.mock(JWTClaimsSet.class);
    doReturn(didMock).when(ssiSettings).getDidConnector();
    doReturn("CorrectAudience").when(didMock).toString();
    doReturn(claimsSetMock).when(jwt).getJWTClaimsSet();
    doReturn(audiences).when(claimsSetMock).getAudience();
    doReturn(futureDate).when(claimsSetMock).getExpirationTime();
    // when
    signedJwtValidator = new SignedJwtValidator(ssiSettings);
    Boolean result = signedJwtValidator.validate(jwt);
    // then
    Assertions.assertTrue(result);
  }

  @Test
  @SneakyThrows
  public void validateInvalidAudience() {
    // given
    List<String> audiences = Arrays.asList("WrongAudience", "WrongAudience");
    Date date = getFutureDate();
    Did didMock = Mockito.mock(Did.class);
    JWTClaimsSet claimsSetMock = Mockito.mock(JWTClaimsSet.class);
    doReturn(didMock).when(ssiSettings).getDidConnector();
    doReturn("CorrectAudience").when(didMock).toString();
    doReturn(claimsSetMock).when(jwt).getJWTClaimsSet();
    doReturn(audiences).when(claimsSetMock).getAudience();
    doReturn(date).when(claimsSetMock).getExpirationTime();
    // when
    signedJwtValidator = new SignedJwtValidator(ssiSettings);
    Boolean result = signedJwtValidator.validate(jwt);
    // then
    Assertions.assertFalse(result);
  }

  @Test
  @SneakyThrows
  public void validateInvalidExpiration() {
    // given
    List<String> audiences = Arrays.asList("WrongAudience", "CorrectAudience");
    Date date = new Date();
    Did didMock = Mockito.mock(Did.class);
    JWTClaimsSet claimsSetMock = Mockito.mock(JWTClaimsSet.class);
    doReturn(didMock).when(ssiSettings).getDidConnector();
    doReturn("CorrectAudience").when(didMock).toString();
    doReturn(claimsSetMock).when(jwt).getJWTClaimsSet();
    doReturn(audiences).when(claimsSetMock).getAudience();
    doReturn(date).when(claimsSetMock).getExpirationTime();
    // when
    signedJwtValidator = new SignedJwtValidator(ssiSettings);
    Boolean result = signedJwtValidator.validate(jwt);
    // then
    Assertions.assertFalse(result);
  }

  @Test
  @SneakyThrows
  public void validateEmptyAudience() {
    // given
    String expectedMsg = "java.lang.NullPointerException";
    Date date = new Date();
    Did didMock = Mockito.mock(Did.class);
    JWTClaimsSet claimsSetMock = Mockito.mock(JWTClaimsSet.class);
    doReturn(didMock).when(ssiSettings).getDidConnector();
    doReturn(null).when(claimsSetMock).getAudience();
    doReturn(date).when(claimsSetMock).getExpirationTime();
    // when
    NullPointerException nullPointerException =
        Assertions.assertThrows(NullPointerException.class, () -> signedJwtValidator.validate(jwt));
    // then
    Assertions.assertTrue(nullPointerException.toString().contains(expectedMsg));
  }

  @Test
  public void validateEmptyExpiration() {
    // given
    List<String> audiences = Arrays.asList("WrongAudience", "CorrectAudience");
    String expectedMsg = "java.lang.NullPointerException";
    Did didMock = Mockito.mock(Did.class);
    JWTClaimsSet claimsSetMock = Mockito.mock(JWTClaimsSet.class);
    doReturn(didMock).when(ssiSettings).getDidConnector();
    doReturn(audiences).when(claimsSetMock).getAudience();
    doReturn(null).when(claimsSetMock).getExpirationTime();
    // when
    NullPointerException nullPointerException =
        Assertions.assertThrows(NullPointerException.class, () -> signedJwtValidator.validate(jwt));
    // then
    Assertions.assertTrue(nullPointerException.toString().contains(expectedMsg));
  }

  private Date getFutureDate() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, 1);
    return cal.getTime();
  }
}

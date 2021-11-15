package ar.lamansys.sgx.auth.user.infrastructure.input.rest.passwordtokenexpiration;

import ar.lamansys.sgx.auth.user.application.getpasswordtokenexpiration.GetPasswordTokenExpiration;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Slf4j
@RestController
@Api(value = "Password-token-expiration", tags = { "Password token expiration" })
public class PasswordTokenExpirationController {

    private GetPasswordTokenExpiration getPasswordTokenExpiration;

    public PasswordTokenExpirationController(GetPasswordTokenExpiration getPasswordTokenExpiration){
        this.getPasswordTokenExpiration = getPasswordTokenExpiration;
    }

    @GetMapping("auth/password-token-expiration")
    public long get(){
        log.debug("No input parameters");
        long result = getPasswordTokenExpiration.execute();
        log.debug("Output -> {}", result);
        return result;
    }
}

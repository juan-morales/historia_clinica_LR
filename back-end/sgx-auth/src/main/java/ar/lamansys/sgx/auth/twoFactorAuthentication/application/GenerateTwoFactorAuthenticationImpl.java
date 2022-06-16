package ar.lamansys.sgx.auth.twoFactorAuthentication.application;

import ar.lamansys.sgx.auth.twoFactorAuthentication.domain.SetTwoFactorAuthenticationBo;
import ar.lamansys.sgx.shared.security.UserInfo;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
@Slf4j
public class GenerateTwoFactorAuthenticationImpl implements GenerateTwoFactorAuthentication {

	public static final String ISSUER = "Historia de Salud Integrada";
	private final UserAuthenticationStorage userAuthenticationStorage;
	private final TwoFactorAuthenticationCypher cypher;

	private String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		Base32 base32 = new Base32();
		return base32.encodeToString(bytes);
	}

	private String getAuthenticatorBarCode(String secretKey, String account) {
		return "otpauth://totp/"
				+ URLEncoder.encode(GenerateTwoFactorAuthenticationImpl.ISSUER + ":" + account, StandardCharsets.UTF_8).replace("+", "%20")
				+ "?secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8).replace("+", "%20")
				+ "&issuer=" + URLEncoder.encode(GenerateTwoFactorAuthenticationImpl.ISSUER, StandardCharsets.UTF_8).replace("+", "%20");
	}

	@Override
	public SetTwoFactorAuthenticationBo run() {
		log.debug("Set two factor authentication");
		Integer userId = UserInfo.getCurrentAuditor();
		String username = userAuthenticationStorage.getUsername(userId);

		String secretKey = generateSecretKey();
		String barCode = getAuthenticatorBarCode(secretKey, username);

		String encryptedSecret = cypher.encrypt(secretKey);

		userAuthenticationStorage.setTwoFactorAuthenticationSecret(userId, encryptedSecret);

		SetTwoFactorAuthenticationBo result = new SetTwoFactorAuthenticationBo(barCode, secretKey);
		log.debug("Output -> {}", result);
		return result;
	}


}

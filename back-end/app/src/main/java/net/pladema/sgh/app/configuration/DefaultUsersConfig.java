package net.pladema.sgh.app.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ar.lamansys.sgx.auth.user.infrastructure.input.service.UserExternalService;
import ar.lamansys.sgx.auth.user.infrastructure.input.service.dto.UserInfoDto;
import ar.lamansys.sgx.shared.admin.AdminConfiguration;
import lombok.extern.slf4j.Slf4j;
import net.pladema.permissions.repository.entity.UserRolePK;
import net.pladema.permissions.repository.enums.ERole;
import net.pladema.permissions.service.RoleService;
import net.pladema.permissions.service.UserAssignmentService;

@Slf4j
@Configuration
@Profile("!test")
public class DefaultUsersConfig {

	@Value("${admin.password}")
	private String adminPassword;

	private final UserExternalService userExternalService;

	private final UserAssignmentService userAssignmentService;

	private final RoleService roleService;

	public DefaultUsersConfig(
			UserExternalService userExternalService,
			UserAssignmentService userAssignmentService,
			RoleService roleService
	) {
		this.userExternalService = userExternalService;
		this.userAssignmentService = userAssignmentService;
		this.roleService = roleService;
	}

	@PostConstruct
	public void init() {
		Map<String, DefaultUserInfoBo> defaultUsers = new HashMap<>();

		defaultUsers.put(AdminConfiguration.ADMIN_USERNAME_DEFAULT, new DefaultUserInfoBo(
				adminPassword,
				List.of(
						new DefaultUserRolBo(ERole.ROOT, UserRolePK.UNDEFINED_ID.intValue()),
						new DefaultUserRolBo(ERole.API_CONSUMER, UserRolePK.UNDEFINED_ID.intValue())
				)
		));

		roleService.updateRolesStore();

		defaultUsers.forEach((key, value) -> {
			UserInfoDto user = userExternalService.getUser(key)
					.orElseGet(() -> createUser(key, value.getPassword()));
			updateUser(user, value);
		});
	}

	private UserInfoDto createUser(String username, String password) {
		userExternalService.registerUser(username, null, password);
		return userExternalService.getUser(username).get();
	}
	
	private void updateUser(UserInfoDto user, DefaultUserInfoBo defaultUserInfoBo) {
		userExternalService.enableUser(user.getUsername());
		userExternalService.updatePassword(user.getUsername(), defaultUserInfoBo.getPassword());
		defaultUserInfoBo.getRoles().forEach(eRole ->
			userAssignmentService.saveUserRole(user.getId(), eRole.getRol(), eRole.getInstitutionId())
		);
		log.info("User updated {}", user.getUsername());
	}

}

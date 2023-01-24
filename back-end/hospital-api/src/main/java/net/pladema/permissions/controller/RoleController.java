package net.pladema.permissions.controller;

import java.util.List;

import ar.lamansys.sgx.shared.featureflags.AppFeature;

import ar.lamansys.sgx.shared.featureflags.application.FeatureFlagsService;

import net.pladema.permissions.repository.enums.ERole;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.permissions.application.getinstitutionalroles.GetInstitutionalRoles;
import net.pladema.permissions.application.getprofessionalroles.GetProfessionalRoles;
import net.pladema.permissions.controller.dto.RoleDto;
import net.pladema.permissions.controller.mappers.RoleDtoMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final GetInstitutionalRoles getInstitutionalRoles;

    private final GetProfessionalRoles getProfessionalRoles;

    private final RoleDtoMapper roleDtoMapper;

	private final FeatureFlagsService featureFlagsService;

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllInstitutionalRoles() {
        log.debug("No input parameters");
        List<RoleDto> result = roleDtoMapper.toListRoleDto(getInstitutionalRoles.execute());
		if (!featureFlagsService.isOn(AppFeature.HABILITAR_DESARROLLO_RED_IMAGENES)){
			result.removeIf(roleDto -> roleDto.getId().equals(ERole.ADMINISTRATIVO_RED_DE_IMAGENES.getId()));
		}
        log.debug("Output -> {}", result);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/professionals")
    public ResponseEntity<List<RoleDto>> getAllProfessionalRoles() {
        log.debug("No input parameters");
        List<RoleDto> result = roleDtoMapper.toListRoleDto(getProfessionalRoles.execute());
		if (!featureFlagsService.isOn(AppFeature.HABILITAR_DESARROLLO_RED_IMAGENES)){
			result.removeIf(roleDto -> roleDto.getId() == ERole.ADMINISTRATIVO_RED_DE_IMAGENES.getId());
		}
        log.debug("Output -> {}", result);
        return ResponseEntity.ok().body(result);
    }

}

package net.pladema.clinichistory.hospitalization.service.evolutionnote;

import ar.lamansys.sgh.clinichistory.domain.ips.DiagnosisBo;
import ar.lamansys.sgh.clinichistory.domain.ips.HealthConditionBo;
import net.pladema.clinichistory.hospitalization.repository.domain.InternmentEpisode;
import net.pladema.clinichistory.hospitalization.service.documents.validation.InternmentDocumentValidator;
import net.pladema.clinichistory.hospitalization.service.evolutionnote.domain.EvolutionNoteBo;
import net.pladema.permissions.repository.enums.ERole;
import net.pladema.sgx.exceptions.PermissionDeniedException;
import net.pladema.sgx.session.infrastructure.input.service.FetchLoggedUserRolesExternalService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EvolutionNoteValidator extends InternmentDocumentValidator {

	private final FetchLoggedUserRolesExternalService fetchLoggedUserRolesExternalService;

	public void validateNursePermissionToLoadProcedures(EvolutionNoteBo evolutionNote) {
		var institutionId = evolutionNote.getInstitutionId();

		var roles = fetchLoggedUserRolesExternalService.execute().collect(Collectors.toList());
		var isNurse = roles.stream().anyMatch(r -> r.institutionId.equals(institutionId)
				&& (r.role.equals(ERole.ENFERMERO)
				|| r.role.equals(ERole.ENFERMERO_ADULTO_MAYOR))
		);
		var isProfessionalOrSpecialist = roles.stream().anyMatch(r -> r.institutionId.equals(institutionId)
				&& (r.role.equals(ERole.ESPECIALISTA_MEDICO)
				|| r.role.equals(ERole.PROFESIONAL_DE_SALUD)
				|| r.role.equals(ERole.ESPECIALISTA_EN_ODONTOLOGIA))
		);

		if(evolutionNote.getProcedures() != null && !evolutionNote.getProcedures().isEmpty()
				&& isNurse
				&& ! isProfessionalOrSpecialist) {
			throw new PermissionDeniedException(
					String.format("Los usuarios con roles %s y %s no tienen permiso para agregar un procedimiento a una nota de evolución",
							ERole.ENFERMERO.getValue(), ERole.ENFERMERO_ADULTO_MAYOR.getValue()));
		}
	}

	public void assertDoesNotHaveEpicrisis(InternmentEpisode internmentEpisode) {
		if(internmentEpisode.getEpicrisisDocId() != null) {
			throw new ConstraintViolationException("Esta internación ya posee una epicrisis", Collections.emptySet());
		}
	}

	public void assertEvolutionNoteValid(EvolutionNoteBo evolutionNote) {
		evolutionNote.validateSelf();
		super.assertDocumentValid(evolutionNote);
	}


	public EvolutionNoteBo verifyEvolutionNoteDiagnosis(EvolutionNoteBo evolutionNote, HealthConditionBo mainDiagnosis) {
		if (evolutionNote.getDiagnosis().isEmpty() && mainDiagnosis != null)
			evolutionNote.setMainDiagnosis(new DiagnosisBo(mainDiagnosis.getSnomed()));
		return evolutionNote;
	}

}
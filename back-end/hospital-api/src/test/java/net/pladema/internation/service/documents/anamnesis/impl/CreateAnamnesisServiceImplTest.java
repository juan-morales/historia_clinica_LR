package net.pladema.internation.service.documents.anamnesis.impl;

import net.pladema.internation.service.InternmentEpisodeService;
import net.pladema.internation.service.NoteService;
import net.pladema.internation.service.documents.DocumentService;
import net.pladema.internation.service.documents.anamnesis.AllergyService;
import net.pladema.internation.service.documents.anamnesis.ClinicalObservationService;
import net.pladema.internation.service.documents.anamnesis.HealthConditionService;
import net.pladema.internation.service.documents.anamnesis.InmunizationService;
import net.pladema.internation.service.documents.anamnesis.MedicationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CreateAnamnesisServiceImplTest {

	private static final String TOKEN = "TOKEN";

	private CreateAnamnesisServiceImpl createAnamnesisServiceImpl;

	@MockBean
	private DocumentService documentService;

	@MockBean
	private InternmentEpisodeService internmentEpisodeService;

	@MockBean
	private NoteService noteService;

	@MockBean
	private HealthConditionService healthConditionService;

	@MockBean
	private AllergyService allergyService;

	@MockBean
	private ClinicalObservationService clinicalObservationService;

	@MockBean
	private InmunizationService inmunizationService;

	@MockBean
	private MedicationService medicationService;

	@Before
	public void setUp() {
		createAnamnesisServiceImpl = new CreateAnamnesisServiceImpl(
				documentService,
				internmentEpisodeService,
				noteService,
				healthConditionService,
				allergyService,
                clinicalObservationService,
				inmunizationService,
				medicationService);
	}


	@Test
	public void test() {
	}
}

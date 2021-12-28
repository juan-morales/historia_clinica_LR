import { Injectable } from '@angular/core';
import { ReplaySubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { HistoricalProblemsFilter } from '../components/historical-problems-filters/historical-problems-filters.component';
import { pushIfNotExists } from '@core/utils/array.utils';
import { momentParseDate } from '@core/utils/moment.utils';
import { ClinicalSpecialtyDto, OutpatientEvolutionSummaryDto, OutpatientSummaryReferenceDto } from '@api-rest/api-model';
import { OutpatientConsultationService } from './../../../../api-rest/services/outpatient-consultation.service';
import { MapperService } from './../../../../presentation/services/mapper.service';
import { REFERENCE_STATES } from '../constants/reference-masterdata';

@Injectable()
export class HistoricalProblemsFacadeService {

	public specialties: ClinicalSpecialtyDto[] = [];
	public professionals: Professional[] = [];
	public problems: Problem[] = [];
	private readonly referenceStates = [REFERENCE_STATES.WITHOUT_REFERENCES, REFERENCE_STATES.WITH_REFERENCES, REFERENCE_STATES.WITH_COUNTERREFERENCE, REFERENCE_STATES.ALL];

	private historicalProblemsSubject = new ReplaySubject<HistoricalProblems[]>(1);
	private historicalProblems$: Observable<HistoricalProblems[]>;
	private historicalProblemsFilterSubject = new ReplaySubject<HistoricalProblemsFilter>(1);
	private historicalProblemsFilter$: Observable<HistoricalProblemsFilter>;
	private originalHistoricalProblems: HistoricalProblems[] = [];

	constructor(
		private readonly outpatientConsultationService: OutpatientConsultationService,
		private readonly mapperService: MapperService,
	) {
		this.historicalProblems$ = this.historicalProblemsSubject.asObservable();
		this.historicalProblemsFilter$ = this.historicalProblemsFilterSubject.asObservable();
	}

	setPatientId(patientId: number): void {
		if (!this.originalHistoricalProblems?.length) {
			this.loadEvolutionSummaryList(patientId);
		}
	}

	public loadEvolutionSummaryList(patientId: number) {
		this.outpatientConsultationService.getEvolutionSummaryList(patientId).pipe(
			tap((outpatientEvolutionSummary: OutpatientEvolutionSummaryDto[]) => this.filterOptions(outpatientEvolutionSummary)),
			map((outpatientEvolutionSummary: OutpatientEvolutionSummaryDto[]) => outpatientEvolutionSummary.length ? this.mapperService.toHistoricalProblems(outpatientEvolutionSummary) : null)
		).subscribe(data => {
			this.originalHistoricalProblems = data;
			this.sendHistoricalProblems(this.originalHistoricalProblems);
		});
	}

	public getHistoricalProblems(): Observable<HistoricalProblems[]> {
		return this.historicalProblems$;
	}

	public getHistoricalProblemsFilter(): Observable<HistoricalProblemsFilter> {
		return this.historicalProblemsFilter$;
	}

	public sendHistoricalProblems(outpatientEvolutionSummary: HistoricalProblems[]): void {
		this.historicalProblemsSubject.next(outpatientEvolutionSummary);
	}

	public sendHistoricalProblemsFilter(newFilter: HistoricalProblemsFilter): void {
		if (this.originalHistoricalProblems && this.originalHistoricalProblems.length) {
			const historichalProblemsCopy = [...this.originalHistoricalProblems];
			const result = historichalProblemsCopy.filter(historicalProblem => (this.filterBySpecialty(newFilter, historicalProblem)
				&& this.filterByProfessional(newFilter, historicalProblem)
				&& this.filterByProblem(newFilter, historicalProblem)
				&& this.filterByConsultationDate(newFilter, historicalProblem)
				&& this.filterByReference(newFilter, historicalProblem)));
			this.historicalProblemsSubject.next(result);
			this.historicalProblemsFilterSubject.next(newFilter);
		}
	}

	private filterBySpecialty(filter: HistoricalProblemsFilter, problem: HistoricalProblems): boolean {
		return (filter.specialty ? problem.specialtyId === filter.specialty : true);
	}

	private filterByProfessional(filter: HistoricalProblemsFilter, problem: HistoricalProblems): boolean {
		return (filter.professional ? problem.consultationProfessionalPersonId === filter.professional : true);
	}

	private filterByProblem(filter: HistoricalProblemsFilter, problem: HistoricalProblems): boolean {
		return (filter.problem ? problem.problemId === filter.problem : true);
	}

	private filterByConsultationDate(filter: HistoricalProblemsFilter, problem: HistoricalProblems): boolean {
		return (filter.consultationDate ? problem.consultationDate ? momentParseDate(problem.consultationDate).isSame(momentParseDate(filter.consultationDate)) : false : true);
	}

	private filterByReference(filter: HistoricalProblemsFilter, problem: HistoricalProblems): boolean {
		switch (filter.referenceStateId) {
			case REFERENCE_STATES.WITH_REFERENCES:
				return problem.reference !== null;

			case REFERENCE_STATES.WITHOUT_REFERENCES:
				return problem.reference === null;

			case REFERENCE_STATES.WITH_COUNTERREFERENCE:
				let result;
				problem.reference?.forEach(problemReference => {
					if (problemReference.counterReference?.counterReferenceNote !== undefined) {
						result = true;
					}
				})
				return result;

			default:
				return true;
		}
	}

	public getFilterOptions() {
		return {
			specialties: this.specialties,
			professionals: this.professionals,
			problems: this.problems,
			referenceStates: this.referenceStates,
		};
	}

	private filterOptions(outpatientEvolutionSummary: OutpatientEvolutionSummaryDto[]): void {
		outpatientEvolutionSummary.forEach(outpatientEvolution => {

			if (outpatientEvolution.clinicalSpecialty) {
				this.specialties = pushIfNotExists(this.specialties, outpatientEvolution.clinicalSpecialty, this.compareSpecialty);
			}

			if (!outpatientEvolution.healthConditions.length) {
				this.problems = pushIfNotExists(this.problems, { problemId: 'Problema no informado', problemDescription: 'Problema no informado' }, this.compareProblems);
			} else {
				outpatientEvolution.healthConditions.forEach(oe => {
					this.problems = pushIfNotExists(this.problems, { problemId: oe.snomed.sctid, problemDescription: oe.snomed.pt }, this.compareProblems);
				});
			}

			this.professionals = pushIfNotExists(this.professionals, { personId: outpatientEvolution.professional.personId, professionalId: outpatientEvolution.professional.id, professionalDescription: `${outpatientEvolution.professional.person.firstName} ${outpatientEvolution.professional.person.lastName}` }, this.compareProfessional);

		});
	}

	private compareSpecialty(specialty: ClinicalSpecialtyDto, specialty2: ClinicalSpecialtyDto): boolean {
		return specialty.id === specialty2.id;
	}

	private compareProfessional(professional: Professional, professional2: Professional): boolean {
		return professional.personId === professional2.personId;
	}

	private compareProblems(problem: Problem, problem2: Problem): boolean {
		return problem.problemId === problem2.problemId;
	}

}

export class Professional {
	personId: number;
	professionalId: number;
	professionalDescription: string;
}

export class Problem {
	problemId: string;
	problemDescription: string;
}

export class HistoricalProblems {
	consultationDate: string;
	consultationEvolutionNote: string;
	consultationProfessionalId: number;
	consultationProfessionalPersonId: number;
	consultationProfessionalName: string;
	document:{
		id: number;
		filename: string;
	};
	problemId: string;
	problemPt: string;
	specialtyId: number;
	specialtyPt: string;
	consultationReasons:
		{
			reasonId: string;
			reasonPt: string;
		}[];
	consultationProcedures:
		{
			procedureDate: string;
			procedureId: string;
			procedurePt: string;
		}[];
	reference: OutpatientSummaryReferenceDto[];
}


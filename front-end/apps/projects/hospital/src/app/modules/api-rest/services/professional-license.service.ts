import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LicenseDataDto, ProfessionalLicenseNumberDto, ValidatedLicenseDataDto, ValidatedLicenseNumberDto } from '@api-rest/api-model';
import { ContextService } from '@core/services/context.service';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class ProfessionalLicenseService {
	private URL_PREFIX = `${environment.apiBase}/institution/${this.contextService.institutionId}/professional-license-number/healthcareprofessional/`;

	constructor(private http: HttpClient, private contextService: ContextService) { }

	saveProfessionalLicensesNumber(healthcareProfessionalId: number, professionalLicenses: ProfessionalLicenseNumberDto[]): Observable<number> {
		const url = this.URL_PREFIX + `${healthcareProfessionalId}`;
		return this.http.post<number>(url, professionalLicenses);
	}

	removeProfessionalLicenseNumber(healthcareProfessionalId: number, professionalLicenses: ProfessionalLicenseNumberDto): Observable<number> {
		const url = this.URL_PREFIX + `${healthcareProfessionalId}/delete`;
		return this.http.post<number>(url, professionalLicenses);
	}

	getLicenseNumberByProfessional(healthcareProfessionalId: number): Observable<ProfessionalLicenseNumberDto[]> {
		const url =  this.URL_PREFIX + `${healthcareProfessionalId}`;
		return this.http.get<ProfessionalLicenseNumberDto[]>(url);
	}

	validateLicenseNumber(healthcareProfessionalId: number, licenseNumbers: LicenseDataDto[]): Observable<ValidatedLicenseDataDto[]> {
		const url =  this.URL_PREFIX + `${healthcareProfessionalId}/validate`;
		return this.http.post<ValidatedLicenseDataDto[]>(url, licenseNumbers);
	}
}

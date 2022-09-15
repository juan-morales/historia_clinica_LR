import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CareLineDto } from '@api-rest/api-model';
import { ContextService } from '@core/services/context.service';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class CareLineService {

	private readonly BASE_URL: string;

	constructor(
		private readonly http: HttpClient,
		private readonly contextService: ContextService,
	) {
		this.BASE_URL = `${environment.apiBase}/institution/${this.contextService.institutionId}`
	}

	getCareLines(): Observable<CareLineDto[]> {
		const url = `${this.BASE_URL}/carelines`;
		return this.http.get<CareLineDto[]>(url);
	}

	getCareLinesBySpecialty(specialtyId: number): Observable<CareLineDto[]> {
		const url = `${this.BASE_URL}/diary-care-lines/${specialtyId} `;
		return this.http.get<CareLineDto[]>(url);
	}
}

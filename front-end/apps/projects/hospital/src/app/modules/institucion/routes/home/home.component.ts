import { Component, OnInit } from '@angular/core';

import { ContextService } from '@core/services/context.service';
import { Observable } from 'rxjs';
import { InstitutionDto } from '@api-rest/api-model';
import { InstitutionService } from '@api-rest/services/institution.service';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AppRoutes } from 'projects/hospital/src/app/app-routing.module';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
	institucion$: Observable<InstitutionDto>;

	constructor(
		private contextService: ContextService,
		private institutionService: InstitutionService,
		private router: Router,
	) { }

	ngOnInit(): void {
		this.contextService.institutionId$.subscribe(id => {
			this.institucion$ = this.institutionService.getInstitutions([id]).pipe(
				map(list => list && list.length ? list[0] : undefined),
			);
		});
	}

	switchInstitution() {
		this.router.navigate([AppRoutes.Home]);
	}
}

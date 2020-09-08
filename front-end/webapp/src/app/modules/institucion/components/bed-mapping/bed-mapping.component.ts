import { Component, OnInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { BedManagement } from 'src/app/modules/camas/routes/home/home.component';
import { Subscription } from 'rxjs';
import { MapperService } from '@presentation/services/mapper.service';
import { BedManagementFacadeService } from '../../services/bed-management-facade.service';
import { BedSummaryDto } from '@api-rest/api-model';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-bed-mapping',
  templateUrl: './bed-mapping.component.html',
  styleUrls: ['./bed-mapping.component.scss']
})
export class BedMappingComponent implements OnInit, OnDestroy {

	@Output() selectedBed = new EventEmitter<number>();

	public bedManagementList: BedManagement[];
	private managementBed$: Subscription;

  	constructor(
		private mapperService: MapperService,
		private bedManagementFacadeService: BedManagementFacadeService
	  ) { }

	ngOnInit(): void {
		this.managementBed$ = this.bedManagementFacadeService.getBedManagement().pipe(
			map((bedsSummary: BedSummaryDto[]) => bedsSummary ? this.mapperService.toBedManagement(bedsSummary) : null)
		).subscribe(data => this.bedManagementList = data);
	}

  	selectBed(bedId: number) {
		this.selectedBed.emit(bedId);
	}

	ngOnDestroy(): void {
		this.managementBed$.unsubscribe();
  	}

}

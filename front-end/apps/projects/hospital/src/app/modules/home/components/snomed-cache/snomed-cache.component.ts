import { Component, OnInit } from '@angular/core';
import { SnomedCacheService } from './snomed-cache.service';
import { TerminologyCSVDto, TerminologyQueueItemDto } from '@api-rest/api-model';
import { Observable } from 'rxjs';


@Component({
	selector: 'app-snomed-cache',
	templateUrl: './snomed-cache.component.html',
	styleUrls: ['./snomed-cache.component.scss']
})
export class SnomedCacheComponent implements OnInit {
	queue$: Observable<TerminologyQueueItemDto[]>;

	constructor(
		private snomedCacheService: SnomedCacheService,
	) { }

	ngOnInit(): void {
		this.queue$ = this.snomedCacheService.queue$.asObservable();
		this.list();
	}

	addCsv(newCsv: TerminologyCSVDto) {
		// console.log('add csv', newCsv);
		this.snomedCacheService.add(newCsv);
	}

	list() {
		this.snomedCacheService.list();
	}

}
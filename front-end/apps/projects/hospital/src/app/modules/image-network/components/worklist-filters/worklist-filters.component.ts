import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { PERSON } from '@core/constants/validation-constants';
import { AppointmentState } from '@turnos/constants/appointment';

@Component({
    selector: 'app-worklist-filters',
    templateUrl: './worklist-filters.component.html',
    styleUrls: ['./worklist-filters.component.scss']
})
export class WorklistFiltersComponent implements OnInit {
    
    @ViewChild('select') select: MatSelect;
    @Input() set appointmentsStates(appointmentsStates: AppointmentState[]) {
        this.appointmentStatesList = appointmentsStates;
        this.initializeFilters();
    };
    @Input() defaultStates?: AppointmentState[];
    @Input() set panelOpenState(panelOpenState: boolean){
        this.panelState = panelOpenState;
        this.clearInputs()
        this.initializeFilters();
    };
    @Output() search = new EventEmitter<SearchFilters>();
    appointmentStatesList: AppointmentState[]
    panelState: boolean;
    filtersForm: UntypedFormGroup;
    states = new UntypedFormControl('');
    selectedStates: string = '';
    allSelected = false;
    searchFilters: SearchFilters;
    
    readonly validations = PERSON;
    
    constructor(private readonly formBuilder: UntypedFormBuilder,
    ) { }

    ngOnInit(): void {
        this.filtersForm = this.formBuilder.group({
			patientName: [''],
			patientIdentification: [''],
            appointmentStates: [null]
		});
    }

    private clearInputs() {
        this.filtersForm?.get('patientName').setValue(null);
        this.filtersForm?.get('patientIdentification').setValue(null);
    }

    private initializeFilters() {
        this.initSearchFilters();
        this.manageStatusCheckboxes();
    }

    applyNameFilter($event: Event){
        this.searchFilters.patientName = ($event.target as HTMLInputElement).value;
        this.search.emit(this.searchFilters);
    }

    applyIdentificationFilter($event: Event){
        this.searchFilters.patientIdentification = ($event.target as HTMLInputElement).value;
        this.search.emit(this.searchFilters);
    }

    onStatusChange(states: MatSelectChange){
        this.setSelectionText(states);
        this.searchFilters.appointmentStates = states.value;
        this.search.emit(this.searchFilters);
    }

    checkSelection() {
        if (this.select) {
            let newStatus = true;
            this.select?.options.forEach((item: MatOption) => {
                if (!item.selected) {
                    newStatus = false;
                }
            });
            this.allSelected = newStatus;
        }
    }

    toggleAllSelection() {
        if (this.allSelected) {
          this.select.options.forEach((item: MatOption) => item.select());
        } else {
          this.select.options.forEach((item: MatOption) => item.deselect());
        }
    }

    manageStatusCheckboxes() {
        this.setDefaultSelection();
        this.checkSelection();
    }

    private initSearchFilters() {
        this.searchFilters = {
            patientName: '',
            patientIdentification: '',
            appointmentStates: this.defaultStates ? this.defaultStates : null,
        }
    }

    private setDefaultSelection(){
        if (this.defaultStates){
            this.select?.options.forEach((item: MatOption) => {
                item.deselect();
                this.defaultStates.map(state => {
                    if ((item.value.id === state.id) && !item.selected) {
                        item._selectViaInteraction();
                    }
                })
            });
        }
    }

    private setSelectionText(states: MatSelectChange){
        this.selectedStates = '';
        states.value.map(state => {
            this.selectedStates = this.selectedStates.concat(state.description.toString(), ", ")
        })
        this.selectedStates = this.selectedStates.trim().slice(0, -1);
    }
    
}

export interface SearchFilters {
    patientName: string;
    patientIdentification: string;
    appointmentStates: AppointmentState[];
}
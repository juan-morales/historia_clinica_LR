import { Component, Input } from '@angular/core';
import { getError, hasError } from '@core/utils/form.utils';
import { isNumberOrDot } from '@core/utils/pattern.utils';
import { FactoresDeRiesgoFormService } from '../../services/factores-de-riesgo-form.service';

@Component({
  selector: 'app-factores-de-riesgo-form',
  templateUrl: './factores-de-riesgo-form.component.html',
  styleUrls: ['./factores-de-riesgo-form.component.scss']
})
export class FactoresDeRiesgoFormComponent {

  @Input() factoresDeRiesgoFormService: FactoresDeRiesgoFormService;
  @Input() showPreloadData: boolean = false;
  @Input() showRecomendation: boolean = false;
  @Input() showErrors: boolean = false;
  readonly isNumberOrDot = isNumberOrDot;
  readonly hasError = hasError;
  readonly getError = getError;

  savePreloadData(save: boolean): void {
    if (save) {
      this.factoresDeRiesgoFormService.savePreloadedRiskFactorsData();
    }
    else {
      this.factoresDeRiesgoFormService.discardPreloadedRiskFactorsData();
    }
  }

}

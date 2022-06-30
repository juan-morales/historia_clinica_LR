import {
	Component,
	Input,
	ElementRef,
	OnChanges,
	SimpleChanges,
} from '@angular/core';
import { ExtensionJSLoaderService } from '@extensions/services/extension-jsloader.service';
@Component({
	selector: 'app-ui-external-component',
	//the template is empty initially, but filled with content at runtime
	template: ``,
	styleUrls: ['./ui-external-component.component.scss'],
})
export class UiExternalComponentComponent implements OnChanges {
	@Input() type: string;
	@Input() params: any;

	constructor(
		//inject the elRef object to interact with the template
		private elRef: ElementRef<HTMLElement>,
		private readonly extensionJSLoaderService: ExtensionJSLoaderService
	) { }

	public ngOnChanges(_changes: SimpleChanges): void {
		//remove and add pluggins every time any data-bound property of a directive changes
		this.removePlugins();
		this.addPlugins();
	}

	private removePlugins(): void {
		const hostElement = this.elRef.nativeElement;
		let child = hostElement.lastElementChild;
		//remove the last child of the hostElement while it exists
		while (!!child) {
			hostElement.removeChild(child);
			child = hostElement.lastElementChild;
		}
	}

	private addPlugins(): void {
		const fetchedComponent = customElements.get(this.params.componentName);
		const hostElement = this.elRef.nativeElement;
		if (fetchedComponent) {
			this.setCustomElement(hostElement, this.params.componentName);
			return;
		}
		const script = this.extensionJSLoaderService.addScriptTag(this.params.url);
		script.addEventListener("load", () => {
			const exist = customElements.get(this.params.componentName);
			if (!exist) {
				console.warn(`Custom element ${this.params.componentName} does not exist`);
				return;
			}
			this.setCustomElement(hostElement, this.params.componentName);
		});
	}

	private setCustomElement(hostElement, elementName) {
		const customElement = document.createElement(elementName);
		customElement.setAttribute('params', JSON.stringify(this.params));
		customElement.setAttribute('data-type', this.type);
		customElement.setAttribute('type', this.type);
		// append the element, so it's attached to the DOM
		hostElement.appendChild(customElement);
	}
}

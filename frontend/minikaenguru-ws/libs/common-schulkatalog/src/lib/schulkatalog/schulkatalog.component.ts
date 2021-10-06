import { Component, OnInit, Inject, Input } from '@angular/core';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';

@Component({
	// tslint:disable-next-line: component-selector
	selector: 'mk-katalog',
	templateUrl: './schulkatalog.component.html',
	styleUrls: ['./schulkatalog.component.css']
})
export class SchulkatalogComponent implements OnInit {

	constructor(@Inject(SchulkatalogConfigService) public readonly config: any) { }

	ngOnInit() {}
}

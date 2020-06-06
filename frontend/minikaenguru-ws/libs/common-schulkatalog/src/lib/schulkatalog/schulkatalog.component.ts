import { Component, OnInit, Inject, Input } from '@angular/core';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { Katalogtyp } from '../domain/entities';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'mk-katalog',
  templateUrl: './schulkatalog.component.html',
  styleUrls: ['./schulkatalog.component.css']
})
export class SchulkatalogComponent implements OnInit {

  devMode: boolean;

  @Input()
  initialTyp: Katalogtyp;

  constructor(@Inject(SchulkatalogConfigService) private config) {}

  ngOnInit() {
	  this.devMode = this.config.devmode;
  }
}

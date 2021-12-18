import { Component, Input, OnInit } from '@angular/core';
import { SchulteilnahmenFacade } from '../../schulteilnahmen/schulteilnahmen.facade';
import { SchulkatalogData } from '../../shared/shared-entities.model';

@Component({
  selector: 'mka-schulkatalogdata',
  templateUrl: './schulkatalogdata.component.html',
  styleUrls: ['./schulkatalogdata.component.css']
})
export class SchulkatalogdataComponent implements OnInit {

  @Input()
  schulkatalogData!: SchulkatalogData;

  constructor(private schulteilnahmenFacade: SchulteilnahmenFacade) { }

  ngOnInit() {
  }
}

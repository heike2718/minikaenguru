import { Component, OnInit, OnDestroy } from '@angular/core';
import { SchulkatalogFacade } from '@minikaenguru-ws/common-schulkatalog';

@Component({
  selector: 'mka-katalogpflege',
  templateUrl: './katalogpflege.component.html',
  styleUrls: ['./katalogpflege.component.css']
})
export class KatalogpflegeComponent implements OnInit, OnDestroy {

  constructor(private schulkatalogFacade: SchulkatalogFacade) { }

  ngOnInit(): void {

	this.schulkatalogFacade.initSchulkatalog('LAND');
  }

  ngOnDestroy(): void {
	  this.schulkatalogFacade.initSchulkatalog('LAND');
  }

}

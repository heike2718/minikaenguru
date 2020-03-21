import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { SchulkatalogFacade, KatalogItem } from '@minikaenguru-ws/common-schulkatalog';

@Component({
  selector: 'mkv-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  devMode: boolean;

  showSchulkatalog: boolean;

  wirdLehrerkonto: boolean;

  selectedKatalogItem: KatalogItem;

  pfadKatalogItem: string;

  constructor(private router: Router, public schulkatalogFacade: SchulkatalogFacade) {

    this.devMode = !environment.production;
	this.showSchulkatalog = false;
	this.wirdLehrerkonto = false;

  }

  ngOnInit() {

    this.schulkatalogFacade.selectedKatalogItem$.subscribe(
      item => {
        if (item ) {
          this.selectedKatalogItem = item;
		  this.pfadKatalogItem = item.pfad;
		  if (item.typ == 'SCHULE') {
			  this.showSchulkatalog = false;
		  }
        }
      }
    );
  }

  gotoKatalogsuche() {
    this.router.navigate([]);
  }

  modusLehrerkonto() {
	this.showSchulkatalog = true;
	this.wirdLehrerkonto = true;
  }

  privatkontoAnlegen() {

  }

  lehrerkontoAnlegen() {

  }

  cancel() {
	  this.router.navigateByUrl('/');
  }

  submitDisabled() {
    if (!this.showSchulkatalog) {
      return false;
    }
    return this.selectedKatalogItem && this.selectedKatalogItem.leaf;
  }

}

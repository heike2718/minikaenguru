import { Component, OnInit, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { SchulkatalogFacade } from '@minikaenguru-ws/common-schulkatalog';
import { InverseKatalogItem } from 'libs/common-schulkatalog/src/lib/domain/entities';

@Component({
  selector: 'mkv-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  devMode: boolean;

  showSchulkatalog: boolean;

  selectedKatalogItem: InverseKatalogItem;

  pfadKatalogItem: string;

  constructor(private router: Router, public schulkatalogFacade: SchulkatalogFacade) {

    this.devMode = !environment.production;
    this.showSchulkatalog = false;

  }

  ngOnInit() {

    this.schulkatalogFacade.selectedKatalogItem$.subscribe(
      item => {
        if (item ) {
          this.selectedKatalogItem = item; 

          switch(item.typ) {
            case 'LAND': this.pfadKatalogItem = item.name; break;
            case 'ORT' : this.pfadKatalogItem = item.parent.name + ' -> ' + item.name; break;
            case 'SCHULE' : this.pfadKatalogItem = item.parent.parent.name + ' -> ' + item.parent.name + ' -> ' + item.name; break;
          }
        }
      }
    );
  }

  gotoKatalogsuche() {
    this.router.navigate([]);
  }

  toggleSchulkatalog() {
    this.showSchulkatalog = !this.showSchulkatalog;
  }

  privatkontoAnlegen() {

  }

  lehrerkontoAnlegen() {

  }

  submitDisabled() {
    if (!this.showSchulkatalog) {
      return false;
    }
    return this.selectedKatalogItem && this.selectedKatalogItem.leaf;
  }

}

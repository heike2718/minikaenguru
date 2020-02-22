import { Component, OnInit, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Component({
  selector: 'mkv-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  devMode: boolean;

  showSchulkatalog: boolean;

  constructor(private router: Router) {

    this.devMode = !environment.production;
    this.showSchulkatalog = false;

  }

  ngOnInit() {
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

}

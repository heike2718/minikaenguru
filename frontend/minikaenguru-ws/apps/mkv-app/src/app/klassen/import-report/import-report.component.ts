import { Component, OnInit } from '@angular/core';
import { environment } from 'apps/mkv-app/src/environments/environment';
import { KlassenFacade } from '../klassen.facade';

@Component({
  selector: 'mkv-import-report',
  templateUrl: './import-report.component.html',
  styleUrls: ['./import-report.component.css']
})
export class ImportReportComponent implements OnInit {

  devMode = !environment.production;

  constructor(public klassenFacade: KlassenFacade) { }

  ngOnInit() {
  }

}

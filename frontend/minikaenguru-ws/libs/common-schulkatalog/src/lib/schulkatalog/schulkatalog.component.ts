import { Component, OnInit, Inject } from '@angular/core';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';

@Component({
  selector: 'mk-katalog',
  templateUrl: './schulkatalog.component.html',
  styleUrls: ['./schulkatalog.component.css']
})
export class SchulkatalogComponent implements OnInit {

  devMode: boolean;

  constructor(@Inject(SchulkatalogConfigService) private config) {

    this.devMode = config.devMode;
  }

  ngOnInit() {
  }
}

import { Component, OnInit, Input, Inject } from '@angular/core';
import { Observable, BehaviorSubject, Subject } from 'rxjs';
import { InverseKatalogItem, Katalogtyp } from '../domain/entities';
import { KatalogService } from '../katalog.service';
import { throws } from 'assert';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';

@Component({
  selector: 'mk-katalog-items-suche',
  templateUrl: './katalog-items-suche.component.html',
  styleUrls: ['./katalog-items-suche.component.css']
})
export class KatalogItemsSucheComponent implements OnInit {

  @Input()
  typ: string;

  devMode: boolean;

  labelForInput: string;
  sucheDescription: string;
  
  katalogItems$: Observable<InverseKatalogItem[]>;
  searchTerm: BehaviorSubject<string>;

  private katalogtyp: Katalogtyp = 'ORT';

  constructor(@Inject(SchulkatalogConfigService) private config, private katalogService: KatalogService) { }

  ngOnInit() {

    this.devMode = this.config.devmode;

    if (this.typ === 'LAND') {
      this.katalogtyp = 'LAND';
      this.labelForInput = 'Land';
      this.sucheDescription = 'das Land';
    }
    if (this.typ === 'ORT') {
      this.katalogtyp = 'ORT';
      this.labelForInput = 'Ort';
      this.sucheDescription = 'den Ort';
    }
    if (this.typ === 'SCHULE') {
      this.katalogtyp = 'SCHULE';
      this.labelForInput = 'Schule';
      this.sucheDescription = 'die Schule';
    }

    this.searchTerm = new BehaviorSubject<string>('');

    console.log('[KatalogItemsSucheComponent] start search with ' + this.searchTerm.value);
    this.katalogItems$ = this.katalogService.searchKatalogItems(this.katalogtyp, this.searchTerm);
  }
}

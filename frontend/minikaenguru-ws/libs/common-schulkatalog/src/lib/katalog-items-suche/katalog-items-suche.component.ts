import { Component, OnInit, Input } from '@angular/core';
import { Observable, BehaviorSubject, Subject } from 'rxjs';
import { InverseKatalogItem, Katalogtyp } from '../domain/entities';
import { KatalogService } from '../katalog.service';
import { throws } from 'assert';

@Component({
  selector: 'mk-katalog-items-suche',
  templateUrl: './katalog-items-suche.component.html',
  styleUrls: ['./katalog-items-suche.component.css']
})
export class KatalogItemsSucheComponent implements OnInit {

  // todo: als Input-Property übergeben!!!!

  @Input()
  typ: string;
  
  katalogItems$: Observable<InverseKatalogItem[]>;
  searchTerm: BehaviorSubject<string>;

  private katalogtyp: Katalogtyp = 'ORT';

  constructor(private katalogService: KatalogService) { 

    if (this.typ === 'LAND') {
      this.katalogtyp = 'LAND';
    }
    if (this.typ === 'ORT') {
      this.katalogtyp = 'ORT';
    }
    if (this.typ === 'SCHULE') {
      this.katalogtyp = 'SCHULE';
    }
  }

  ngOnInit() {

    this.searchTerm = new BehaviorSubject<string>('');

    console.log('[KatalogItemsSucheComponent] start search with ' + this.searchTerm.value);
    this.katalogItems$ = this.katalogService.searchKatalogItems(this.katalogtyp, this.searchTerm);
  }
}

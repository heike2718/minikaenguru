import { Component, OnInit } from '@angular/core';
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
  private katalogtyp: Katalogtyp = 'ORT';
  katalogItems$: Observable<InverseKatalogItem[]>;
  searchTerm: BehaviorSubject<string>;

  constructor(private katalogService: KatalogService) { }

  ngOnInit() {

    this.searchTerm = new BehaviorSubject<string>('');

    console.log('[KatalogItemsSucheComponent] start search with ' + this.searchTerm.value);
    this.katalogItems$ = this.katalogService.searchKatalogItems(this.katalogtyp, this.searchTerm);
  }
}

import { Component, OnInit, Input } from '@angular/core';
import { KatalogItem } from '../domain/entities';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { selectKatalogItem } from '../+state/schulkatalog.actions';

@Component({
  selector: 'mk-katalog-item',
  templateUrl: './katalog-item.component.html',
  styleUrls: ['./katalog-item.component.css']
})
export class KatalogItemComponent implements OnInit {

  @Input()
  katalogItem: KatalogItem;

  @Input()
  devMode: boolean;

  anzahlText: string;

  constructor(private store: Store<SchulkatalogState>) { }

  ngOnInit() {

    switch (this.katalogItem.typ) {
      case 'LAND': this.anzahlText = 'Anzahl Orte:'; break;
      case 'ORT': this.anzahlText = 'Anzahl Schulen:'; break;
      default: this.anzahlText = '';
    }

  }

  selectTheItem() {
    this.store.dispatch(selectKatalogItem({data: this.katalogItem}));
  }
}


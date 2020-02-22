import { Component, OnInit, Input } from '@angular/core';
import { InverseKatalogItem } from '../domain/entities';

@Component({
  selector: 'mk-katalog-item',
  templateUrl: './katalog-item.component.html',
  styleUrls: ['./katalog-item.component.css']
})
export class KatalogItemComponent implements OnInit {

  @Input()
  katalogItem: InverseKatalogItem;

  @Input()
  devMode: boolean;

  anzahlText: string;

  constructor() { }

  ngOnInit() {

    switch(this.katalogItem.typ) {
      case 'LAND': this.anzahlText = 'Anzahl Orte:'; break;
      case 'ORT': this.anzahlText = 'Anzahl Schulen:'; break;
      default: this.anzahlText = '';
    }

  }

  selectTheItem() {
    console.log('KatalogItem ' + this.katalogItem.name + ' selected');
  }

}

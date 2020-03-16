import { Component, OnInit, Input, Inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import { SchulkatalogConfigService } from '../configuration/schulkatalog-config';
import { SchulkatalogFacade } from '../application-services/schulkatalog.facade';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { initKatalogtyp } from '../+state/schulkatalog.actions';
import { selectSelectedKatalogItem } from '../+state/schulkatalog.selectors';

@Component({
  // tslint:disable-next-line: component-selector
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

  private selectedKatalogItem$ = this.store.select(selectSelectedKatalogItem);

  //   katalogItems$: Observable<KatalogItem[]>;
  searchTerm: BehaviorSubject<string>;

  private katalogtyp: Katalogtyp = 'ORT';

  constructor(@Inject(SchulkatalogConfigService) private config,
    private store: Store<SchulkatalogState>,
    public schulkatalogFacade: SchulkatalogFacade) { }

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

    this.store.dispatch(initKatalogtyp({ data: this.katalogtyp }))
    this.searchTerm = new BehaviorSubject<string>('');

    this.schulkatalogFacade.searchKatalogItems(this.katalogtyp, this.searchTerm);

    this.selectedKatalogItem$.subscribe(
      item => this.handleItemSelected(item)
    );
  }


  onKeyup(event) {

    const value = event.value;
    console.log('[event.value=' + value + ']')

  }

  private handleItemSelected(selectedItem: KatalogItem) {

    if (selectedItem && !selectedItem.leaf) {

      switch (this.katalogtyp) {
        case 'LAND': break;
        case 'ORT': break;
        case 'SCHULE': break;
      }

    }

    if (selectedItem) {
      console.log('[KatalogItemsSucheComponent]: [' + selectedItem.name + ',' + selectedItem.kuerzel + '] has been selected');
    }
  }
}

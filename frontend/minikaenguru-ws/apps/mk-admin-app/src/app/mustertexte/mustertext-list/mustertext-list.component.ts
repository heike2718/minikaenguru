import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { Subscription } from 'rxjs';
import { MUSTRETEXT_KATEGORIE } from '../../shared/shared-entities.model';
import { MustertexteFacade } from '../mustertexte.facade';

@Component({
  selector: 'mka-mustertext-list',
  templateUrl: './mustertext-list.component.html',
  styleUrls: ['./mustertext-list.component.css']
})
export class MustertextListComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';

  filterKriterium: MUSTRETEXT_KATEGORIE = 'UNDEFINED';

  filter: MUSTRETEXT_KATEGORIE[] = ['UNDEFINED', 'MAIL', 'NEWSLETTER'];

  constructor(public mustertextFacade: MustertexteFacade, 
    private router: Router) { }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  filterKriteriumChanged(): void {

    this.mustertextFacade.selectFilter(this.filterKriterium);

  }

  loadMustertexte(): void {

    this.mustertextFacade.loadMustertexte();

  }

  addMustertext(): void {
		this.mustertextFacade.createNewMustertext();
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}
}

import { Component, OnInit } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { VeranstalterSuchanfrage, Veranstalter } from '../veranstalter.model';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mka-veranstalter-list',
	templateUrl: './veranstalter-list.component.html',
	styleUrls: ['./veranstalter-list.component.css']
})
export class VeranstalterListComponent implements OnInit {


	devMode = environment.envName === 'DEV';

	veranstalters$: Observable<Veranstalter[]> = this.veranstalterFacade.veranstalters$;
	loading$: Observable<boolean> = this.veranstalterFacade.loading$;
	sucheFinished$: Observable<boolean> = this.veranstalterFacade.sucheFinished$;


	constructor(private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void { }

	listeLeeren() {
		this.veranstalterFacade.trefferlisteLeeren();
	}

}

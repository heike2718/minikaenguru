import { Component, OnInit, OnDestroy } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { Subscription, Observable } from 'rxjs';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-klasse-editor',
	templateUrl: './klasse-editor.component.html',
	styleUrls: ['./klasse-editor.component.css']
})
export class KlasseEditorComponent implements OnInit, OnDestroy {

	schule$: Observable<Schule> = this.lehrerFacade.selectedSchule$;

	name: string;

	submitted = false;

	warntextDuplikat = '';

	private saveInProgress = false;

	private modelSubscription: Subscription;

	constructor(private router: Router,
		private klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade) { }

	ngOnInit(): void {

		this.modelSubscription = this.klassenFacade.editorModel$.subscribe(
			em => {
				if (em) {
					this.name = em.name;
				} else {
					const url = '/lehrer/dashboard';
					this.router.navigateByUrl(url);
				}
			}
		);
	}

	ngOnDestroy(): void {

		if (this.modelSubscription) {
			this.modelSubscription.unsubscribe();
		}

	}

	submitDisabled(): boolean {

		if (this.saveInProgress) {
			return true;
		}

		if (this.name || this.name.length === 0) {
			return true;
		}

		return false;
	}


	onSubmit(): void {
		this.submitted = true;
	}

	onCancel(): void {

	}
}

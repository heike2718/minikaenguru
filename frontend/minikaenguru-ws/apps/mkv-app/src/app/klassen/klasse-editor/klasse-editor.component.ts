import { Component, OnInit, OnDestroy } from '@angular/core';
import { KlassenFacade } from '../klassen.facade';
import { Subscription, Observable } from 'rxjs';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { Router, ActivatedRoute } from '@angular/router';
import { MessageService } from '@minikaenguru-ws/common-messages';

@Component({
	selector: 'mkv-klasse-editor',
	templateUrl: './klasse-editor.component.html',
	styleUrls: ['./klasse-editor.component.css']
})
export class KlasseEditorComponent implements OnInit, OnDestroy {

	schule$: Observable<Schule | undefined> = this.lehrerFacade.selectedSchule$;

	name: string = '';

	submitted = false;

	warntextDuplikat = '';

	private uuid: string = '';

	private schulkuerzel: string = '';

	private saveInProgress = false;

	private modelSubscription: Subscription = new Subscription();

	private routeParamsSubcription: Subscription = new Subscription();

	private schuleSubscription: Subscription = new Subscription();

	constructor(private router: Router,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade) { }

	ngOnInit(): void {

		this.routeParamsSubcription = this.route.params.subscribe(
			p => {
				this.uuid = p['id'];
			}
		);

		this.schuleSubscription = this.schule$.subscribe(
			s => {
				if (s) {
					this.schulkuerzel = s.kuerzel;
				}
			}
		);

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

		this.routeParamsSubcription.unsubscribe();
		this.schuleSubscription.unsubscribe();
		this.modelSubscription.unsubscribe();
	}

	submitDisabled(): boolean {

		if (this.saveInProgress) {
			return true;
		}

		if (!this.name || this.name.length === 0) {
			return true;
		}

		if (!this.schulkuerzel || !this.uuid) {
			return true;
		}

		return false;
	}


	onSubmit(): void {
		this.submitted = true;
		if (this.uuid === 'neu') {
			this.klassenFacade.insertKlasse(this.uuid, this.schulkuerzel, { name: this.name });
		} else {
			this.klassenFacade.updateKlasse(this.uuid, this.schulkuerzel, { name: this.name });
		}
	}


	addKlasse(): void {
		this.saveInProgress = false;
		this.messageService.clear();
		this.name = '';
		this.klassenFacade.startCreateKlasse();
	}


	onCancel(): void {
		this.messageService.clear();
		this.klassenFacade.cancelEditKlasse();

		if (this.schulkuerzel) {
			this.router.navigateByUrl('/klassen/' + this.schulkuerzel);
		} else {
			this.router.navigateByUrl('/lehrer/schulen');
		}
	}
}

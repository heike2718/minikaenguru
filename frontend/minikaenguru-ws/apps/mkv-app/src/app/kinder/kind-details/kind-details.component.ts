import { Component, OnInit, Input, ViewChild, TemplateRef, OnDestroy } from '@angular/core';
import { Kind, kindToString } from '@minikaenguru-ws/common-components';
import { Router } from '@angular/router';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { KinderFacade } from '../kinder.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Subscription } from 'rxjs';
import { KlassenFacade } from '../../klassen/klassen.facade';
import { LoesungszettelFacade } from '../../loesungszettel/loesungszettel.facade';
import { UrkundenFacade } from '../../urkunden/urkunden.facade';
import { User, STORAGE_KEY_USER } from '@minikaenguru-ws/common-auth';
import { environment } from '../../../environments/environment';
import { LehrerFacade } from '../../lehrer/lehrer.facade';

@Component({
	selector: 'mkv-kind-details',
	templateUrl: './kind-details.component.html',
	styleUrls: ['./kind-details.component.css']
})
export class KindDetailsComponent implements OnInit, OnDestroy {

	@ViewChild('loeschenWarndialog')
	loeschenWarndialog: TemplateRef<HTMLElement>;

	@Input()
	kind: Kind

	showKlasseWechselnButton = false;

	selectedKlasse$ = this.klassenFacade.selectedKlasse$;

	btnUrkundeLabel = 'Urkunde';

	btnUrkundeTooltip = 'Urkunde erstellen';

	showHinweisUrkunde = false;

	zugangUnterlagen = false;

	private klasseSubscription: Subscription;

	private klasseUuid: string;

	private klassenSubscription: Subscription;

	private zugangUnterlagenSubscription: Subscription;

	titel: string;

	constructor(private router: Router,
		private modalService: NgbModal,
		private kinderFacade: KinderFacade,
		private klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade,
		private privatveranstalterFacade: PrivatveranstalterFacade,
		private loesungszettelFacade: LoesungszettelFacade,
		private logger: LogService
	) { }

	ngOnInit(): void {

		this.titel = kindToString(this.kind);

		const user: User = this.readUser();

		if (user && user.rolle === 'LEHRER') {
			this.btnUrkundeLabel = 'Urkunde korrigieren';
			this.btnUrkundeTooltip = 'Urkunde dieses Kindes korrigieren';
			this.showHinweisUrkunde = this.kind.punkte && this.kind.punkte.loesungszettelId !== 'neu';
			this.zugangUnterlagenSubscription = this.lehrerFacade.hatZugangZuUnterlagen$.subscribe(
				z => this.zugangUnterlagen = z
			);
		} else {
			this.zugangUnterlagenSubscription = this.privatveranstalterFacade.hatZugangZuUnterlagen$.subscribe(
				z => this.zugangUnterlagen = z
			);
		}

		this.klasseSubscription = this.selectedKlasse$.subscribe(
			kl => {
				if (kl) {
					this.klasseUuid = kl.uuid;
				}
			}
		);

		this.klassenSubscription = this.klassenFacade.klassen$.subscribe(

			klassen => {
				if (klassen && klassen.length > 1) {
					this.showKlasseWechselnButton = true;
				} else {
					this.showKlasseWechselnButton = false;
				}
			}
		);


	}

	ngOnDestroy(): void {
		if (this.klasseSubscription) {
			this.klasseSubscription.unsubscribe();
		}
		if (this.klassenSubscription) {
			this.klassenSubscription.unsubscribe();
		}
		if (this.zugangUnterlagenSubscription) {
			this.zugangUnterlagenSubscription.unsubscribe();
		}
	}

	editKind(): void {
		this.kinderFacade.editKind(this.kind);
		const url = '/kinder/kind-editor/' + this.kind.uuid;

		if (this.klasseUuid) {
			this.router.navigate([url], { queryParams: { klasseUuid: this.klasseUuid } });
		} else {
			this.router.navigateByUrl(url);
		}
	}

	klasseWechseln() {
		this.kinderFacade.startKlassenwechsel(this.kind);
	}

	deleteKind(): void {

		this.modalService.open(this.loeschenWarndialog, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {

			if (result === 'ja') {
				this.kinderFacade.deleteKind(this.kind, this.klasseUuid);
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});
	}

	editLoesungszettel(): void {

		this.kinderFacade.selectKind(this.kind);

		if (this.kind.punkte) {
			this.loesungszettelFacade.loadLoesungszettel(this.kind);
		} else {
			this.loesungszettelFacade.createNewLoesungszettel(this.kind);
		}

		this.router.navigateByUrl('/loesungszettel');
	}

	urkundeErstellen(): void {
		this.kinderFacade.selectKind(this.kind);
		this.router.navigateByUrl('/einzelurkunden');
	}


	private readUser(): User {
		const obj = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);
		if (obj) {
			return JSON.parse(obj);
		}
		return undefined;
	}

}

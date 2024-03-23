import { Component, OnInit, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { Veranstalter } from '../veranstalter.model';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { SchulteilnahmenFacade } from '../../schulteilnahmen/schulteilnahmen.facade';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { modalOptions } from '@minikaenguru-ws/common-components';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mka-veranstalter-details',
	templateUrl: './veranstalter-details.component.html',
	styleUrls: ['./veranstalter-details.component.css']
})
export class VeranstalterDetailsComponent implements OnInit, OnDestroy {

	selectedVeranstalter$: Observable<Veranstalter | undefined> = this.veranstalterFacade.selectedVeranstalter$;

	teilnahmenummernAsString: string = '';
	veranstalterFullName: string = 'Der Veranstalter';

	@ViewChild('dialogNewsletterDeaktivieren')
	dialogNewsletterDeaktivieren!: TemplateRef<HTMLElement>;

	private veranstalter!: Veranstalter;
	private veranstalterSubscription: Subscription = new Subscription();

	constructor(private router: Router
		, private veranstalterFacade: VeranstalterFacade
		, private schulteilnahmenFacade: SchulteilnahmenFacade
		, private modalService: NgbModal
		, private logger: LogService) { }

	ngOnInit(): void {

		this.teilnahmenummernAsString = '';

		this.veranstalterSubscription = this.selectedVeranstalter$.subscribe(
			veranstalter => {
				if (!veranstalter) {
					this.router.navigateByUrl('/veranstalter');
				} else {
					this.teilnahmenummernAsString = this.getTeilnahmenummernAsString(veranstalter);
					this.veranstalter = veranstalter;
					this.veranstalterFullName = veranstalter.fullName;
				}
			}
		);

	}

	ngOnDestroy(): void {
		this.veranstalterSubscription.unsubscribe();
	}

	private getTeilnahmenummernAsString(veranstalter: Veranstalter): string {

		let result = '';
		veranstalter.teilnahmenummern.forEach(
			t => {
				result += t;
				result += ' ';
			}
		);

		return result;
	}

	gotoTeilnahmen(teilnahmenummer: string): void {

		if (this.veranstalter && this.veranstalter.rolle && this.veranstalter.rolle === 'LEHRER') {
			this.schulteilnahmenFacade.findOrLoadSchuleAdminOverview(teilnahmenummer);
		} else {
			this.veranstalterFacade.findOrLoadPrivatteilnahmeAdminOverview(teilnahmenummer);
		}
	}

	newsletterDeaktivieren(): void {

		this.modalService.open(this.dialogNewsletterDeaktivieren, modalOptions).result.then((result) => {

			if (result === 'OK') {
				this.forceNewsletterDeaktivieren();
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});

	}

	private forceNewsletterDeaktivieren(): void {

		this.veranstalterFacade.newsletterDeaktivieren(this.veranstalter);
	}
}



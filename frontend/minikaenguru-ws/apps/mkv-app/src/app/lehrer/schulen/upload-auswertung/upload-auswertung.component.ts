import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { initialUploadComponentModel, UploadComponentModel } from '@minikaenguru-ws/common-components';
import { LogService } from '@minikaenguru-ws/common-logging';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { environment } from 'apps/mkv-app/src/environments/environment';
import { combineLatest, Subscription } from 'rxjs';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';
import { LehrerFacade } from '../../lehrer.facade';
import { Schule } from '../schulen.model';

@Component({
  selector: 'mkv-upload-auswertung',
  templateUrl: './upload-auswertung.component.html',
  styleUrls: ['./upload-auswertung.component.css']
})
export class UploadAuswertungComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';

  #lehrerFacade = inject(LehrerFacade);
  #wettbewerbFacade = inject(WettbewerbFacade);
  #logger = inject(LogService);
  #router = inject(Router);

  // selectedSchule$ = this.#lehrerFacade.selectedSchule$;

  uploadModel: UploadComponentModel = initialUploadComponentModel;

  tooltipBtnSchuluebersicht: string = '';

	subUrl = '';

  private schuleUndWettbewerbSubscription: Subscription = new Subscription();

  schule!: Schule;
  

  ngOnInit(): void {

    this.tooltipBtnSchuluebersicht = 'Übersicht';

    this.schuleUndWettbewerbSubscription = combineLatest([this.#lehrerFacade.selectedSchule$, this.#wettbewerbFacade.aktuellerWettbewerb$]).subscribe(
      result => {

        if (result[0] && result[1]) {

          this.schule = result[0];
          this.tooltipBtnSchuluebersicht = 'Übersicht ' + this.schule.name;

          this.#logger.debug('schule: ' + this.schule.name);

          this.subUrl = '/uploads/auswertung/' + result[1].jahr + '/' + this.schule.kuerzelLand + '/' + this.schule.kuerzel;
					this.uploadModel = {
						subUrl: this.subUrl
						, titel: ''
						// , accept: '.*'
						, accept: '.ods, .xlsx'
						, maxSizeBytes: 2097152
						// , maxSizeBytes: 61440
						, errorMessageSize: 'Die Datei ist zu groß. Die maximale erlaubte Größe ist 2 MB'
						, acceptMessage: 'Erlaubte Dateitypen: xlsx, ods. Achtung! Das alte MS-Office-Format xls wird nicht unterstützt.'
					};
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.schuleUndWettbewerbSubscription.unsubscribe();      
  }

  onDateiAusgewaehlt(): void {

    if (this.uploadModel.subUrl !== '') {
		  // machen mal nix
    } else {
      this.#logger.warn('Upload nicht möglich: queryParams nicht gesetzt! (sollte nicht vorkommen, alle Varianten abgedeckt');
    }
	}

	onResponse(rp: ResponsePayload | any): void {
		if (rp && this.schule) {
			// this.messageService.showMessage(rp.message);
      this.#lehrerFacade.handleAuswertungstabelleHochgeladen(this.schule, rp);
		}
	}

  gotoDashboard(): void {
		this.#router.navigateByUrl('/lehrer/dashboard');
	}

  gotoSchuleDashboard(): void {

		if (this.schule) {
			const url = '/lehrer/schule-dashboard/' + this.schule.kuerzel;
      this.#router.navigateByUrl(url);
		}
	}

  gotoBewertung() {
		this.#router.navigateByUrl('/feedback/wettbewerb');
	}
}

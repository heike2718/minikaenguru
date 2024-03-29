import { Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'apps/mkv-app/src/environments/environment';
import { Subscription, combineLatest } from 'rxjs';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { KlassenFacade } from '../klassen.facade';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { UploadComponentModel } from '@minikaenguru-ws/common-components';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { LogService } from '@minikaenguru-ws/common-logging';
import { modalOptions } from '@minikaenguru-ws/common-components';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'mkv-upload-klassenlisten',
  templateUrl: './upload-klassenlisten.component.html',
  styleUrls: ['./upload-klassenlisten.component.css']
})
export class UploadKlassenlistenComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';

  @ViewChild('dialogUploadFormatInfo')
	dialogUploadFormatInfo!: TemplateRef<HTMLElement>;

  schule!: Schule;

  uploadModel!: UploadComponentModel;

	spracheEnglisch!: boolean;

  nachnameVerbergen!: boolean;

	subUrl = '';

  textSprache = 'Die Kinder werden mit der Sprache deutsch importiert.';

  textNachname = 'Die Nachnamen werden in das Feld Nachname importiert und erscheinen auf der Urkunde.';

  tooltipBtnSchuluebersicht: string = 'Übersicht';

  private schuleUndWettbewerbSubscription: Subscription = new Subscription();


  constructor(private router: Router,
     private wettbewerbFacade: WettbewerbFacade,
     public klassenFacade: KlassenFacade,
     private lehrerFacade: LehrerFacade,
     private modalService: NgbModal,
     private logger: LogService) { }

  ngOnInit(): void {

    this.schuleUndWettbewerbSubscription = combineLatest([this.wettbewerbFacade.aktuellerWettbewerb$, this.lehrerFacade.selectedSchule$]).subscribe(
      result => {

        if (result[0] && result[1]) {
          const w = result[0];
          const s: Schule = result[1];
          const jahr = w.jahr;
          this.schule = s;
          this.subUrl = '/uploads/klassenlisten/' + jahr + '/' + s.kuerzelLand + '/' + s.kuerzel;
          this.tooltipBtnSchuluebersicht = 'Übersicht ' + this.schule.name;
          this.uploadModel = {
            subUrl: this.subUrl + '?nachnameAlsZusatz=false&sprache=de'
            , titel: ''
            // , accept: '.*'
            , accept: '.csv, .ods, .xlsx'
            , maxSizeBytes: 1048576
            // , maxSizeBytes: 61440
            , errorMessageSize: 'Die Datei ist zu groß. Die maximale erlaubte Größe ist 1 MB'
            , acceptMessage: 'Erlaubte Dateitypen: xlsx, ods, csv. Achtung! Das alte MS-Office-Format xls wird nicht unterstützt.'
          };
        } else {
          this.router.navigateByUrl('/lehrer/schulen');
        }        
      }
    );
  }

  ngOnDestroy(): void {

    if (this.schuleUndWettbewerbSubscription) {
      this.schuleUndWettbewerbSubscription.unsubscribe();
    }
  }

  onDateiAusgewaehlt(): void {

    if (this.uploadModel.subUrl !== '') {
		  this.klassenFacade.dateiAusgewaelt();
    } else {
      this.logger.warn('Upload nicht möglich: queryParams nicht gesetzt! (sollte nicht vorkommen, alle Varianten abgedeckt');
    }
	}

	onResponse(rp: ResponsePayload | any): void {

		if (rp) {
			this.klassenFacade.klassenlisteImportiert(rp);
		}
	}

  onCheckboxSpracheClicked(event: boolean) {
    this.spracheEnglisch = event;
		this.uploadModel = {...this.uploadModel, subUrl: this.getQueryParameters()};
    this.textSprache = this.spracheEnglisch ? 'Die Kinder werden mit der Sprache <strong>englisch</strong> importiert.' : 'Die Kinder werden mit der Sprache deutsch importiert.';
	}

  onCheckboxNachnameVerbergenClicked(event: boolean) {
    this.nachnameVerbergen = event;
		this.uploadModel = {...this.uploadModel, subUrl: this.getQueryParameters()};
    this.textNachname = this.nachnameVerbergen ? 'Die Nachnamen werden in das Feld Zusatz importiert und erscheinen <strong>nicht</strong> auf der Urkunde.' : 'Die Nachnamen werden in das Feld Nachname importiert und erscheinen auf der Urkunde.';
	}

  showInfoUploadFormat() {
		this.modalService.open(this.dialogUploadFormatInfo, modalOptions).result.then((_result) => {
			
			// do nothing
	  });
	}

  gotoKlassen(): void {
    this.router.navigateByUrl('/klassen/' + this.schule.kuerzel);
  }

  gotoDashboard(): void {

		let url = '/lehrer/dashboard';
		if (this.schule) {
			url = '/lehrer/schule-dashboard/' + this.schule.kuerzel;
		}

		this.router.navigateByUrl(url);
	}

  private getQueryParameters(): string {

    if (this.spracheEnglisch && this.nachnameVerbergen) {
      return this.subUrl + '?nachnameAlsZusatz=true&sprache=en'
    }

    if (!this.spracheEnglisch && !this.nachnameVerbergen) {
      return this.subUrl + '?nachnameAlsZusatz=false&sprache=de'
    }

    if (this.spracheEnglisch && !this.nachnameVerbergen) {
      return this.subUrl + '?nachnameAlsZusatz=false&sprache=en'
    }

    if (!this.spracheEnglisch && this.nachnameVerbergen) {
      return this.subUrl + '?nachnameAlsZusatz=true&sprache=de'
    }

    return '';
  }
}

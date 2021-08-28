import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'apps/mkv-app/src/environments/environment';
import { Subscription, combineLatest } from 'rxjs';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { KlassenFacade } from '../klassen.facade';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { UploadComponentModel } from '@minikaenguru-ws/common-components';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { map } from 'rxjs/operators';

@Component({
  selector: 'mkv-upload-klassenlisten',
  templateUrl: './upload-klassenlisten.component.html',
  styleUrls: ['./upload-klassenlisten.component.css']
})
export class UploadKlassenlistenComponent implements OnInit, OnDestroy {

  devMode = environment.envName = 'DEV';

  schule: Schule;

  uploadModel: UploadComponentModel;

	spracheEnglisch: boolean;

  nachnameVerbergen: boolean;

	subUrl = '';

  textSprache = 'Die Kinder werden mit der Sprache deutsch importiert.';

  textNachname = 'Die Nachnamen werden in das Feld Nachname importiert und erscheinen auf der Urkunde.';

  private schuleUndWettbewerbSubscription: Subscription;


  constructor(private router: Router,
     private wettbewerbFacade: WettbewerbFacade,
     public klassenFacade: KlassenFacade,
     private lehrerFacade: LehrerFacade) { }

  ngOnInit(): void {

    this.schuleUndWettbewerbSubscription = combineLatest([this.wettbewerbFacade.aktuellerWettbewerb$, this.lehrerFacade.selectedSchule$]).subscribe(
      result => {

        const w = result[0];
        const s: Schule = result[1];

        if (w && s) {
          const jahr = w.jahr;
          this.schule = s;
          this.subUrl = '/uploads/klassenlisten/' + jahr + '/' + s.kuerzelLand + '/' + s.kuerzel;
          this.uploadModel = {
            subUrl: this.subUrl + '?nachnameAlsZusatz=false&sprache=de'
            , titel: ''
            // , accept: '.*'
            , accept: '.csv, .ods, .xls, .xlsx'
            , maxSizeBytes: 2097152
            // , maxSizeBytes: 61440
            , errorMessageSize: 'Die Datei ist zu groß. Die maximale erlaubte Größe ist 2 MB'
            , acceptMessage: 'Erlaubte Dateitypen: csv, Excel, OpenOffice, LibreOffice'
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

	onCheckboxSpracheChanged(_event$): void {

		this.uploadModel = {...this.uploadModel, subUrl: this.getQueryParameters()};

    this.textSprache = this.spracheEnglisch ? 'Die Kinder werden mit der Sprache englisch importiert.' : 'Die Kinder werden mit der Sprache deutsch importiert.';

	}

	onCheckboxNachnameVerbergenChanged(_event$): void {

		this.uploadModel = {...this.uploadModel, subUrl: this.getQueryParameters()};

    this.textNachname = this.nachnameVerbergen ? 'Die Nachnamen werden in das Feld Zusatz importiert und erscheinen nicht auf der Urkunde.' : 'Die Nachnamen werden in das Feld Nachname importiert und erscheinen auf der Urkunde.';
	}  

  onDateiAusgewaehlt(event$): void {

		this.klassenFacade.dateiAusgewaelt();
	}

	onResponse(rp: ResponsePayload | any): void {

		if (rp) {
			this.klassenFacade.klassenlisteImportiert(rp);
		}
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
  }
}

import { createAction, props } from '@ngrx/store';
import { AktuelleMeldung } from '../aktuelle-meldung.model';


export const aktuelleMeldungGeladen = createAction(
	'[AktuelleMeldungFacade] ladeAktuelleMeldung',
	props<{aktuelleMeldung: AktuelleMeldung}>()
);

export const aktuelleMeldungGespeichert = createAction(
	'[AktuelleMeldungFacade] aktuelleMeldungSpeichern',
	props<{aktuelleMeldung: AktuelleMeldung}>()
);

export const aktuelleMeldungGeloescht = createAction(
	'[AktuelleMeldungFacade] aktuelleMeldungLoeschen'
);

export const resetAktuelleMeldung = createAction(
	'[NavbarComponent] - aktMeldung login/logout'
);




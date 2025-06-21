import { createAction, props } from '@ngrx/store';
import { AktuelleMeldung } from '../aktuelle-meldung.model';


export const aktuelleMeldungGeladen = createAction(
	'[AktuelleMeldungFacade] ladeAktuelleMeldung',
	props<{aktuelleMeldung: AktuelleMeldung}>()
);

export const aktuelleMeldungReset = createAction(
	'[AktuelleMeldungFacade] resetState'
);



import { NgbDate } from '@ng-bootstrap/ng-bootstrap';

export type Urkundenart = 'KAENGURUSPRUNG' | 'TEILNAHME';

export type Farbschema = 'BLUE' | 'GREEN' | 'ORANGE';

export interface UrkundenauftragEinzelkind {
	readonly urkundenart?: Urkundenart;
	readonly kindUuid: string;
	readonly dateString?: string;
	readonly farbschema?: Farbschema;
};

export interface UrkundenauftragSchule {
	readonly schulkuerzel: string;
	readonly dateString?: string;
	readonly farbschema?: Farbschema;
}

export interface UrkundeDateModel {
	readonly minDate: NgbDate,
	readonly maxDate: NgbDate,
	readonly selectedDate: string
};


export function getLabelFarbe(farbschema: Farbschema): string {


	if (!farbschema) {
		return '';
	}

	switch (farbschema) {
		case 'BLUE': return 'blaue';
		case 'GREEN': return 'grüne';
		case 'ORANGE': return 'orange';
		default: return '';
	}


}

export function getLabelUrkundenart(urkundenart: Urkundenart): string {


	if (!urkundenart) {
		return 'Urkunde';
	}

	switch (urkundenart) {
		case 'TEILNAHME': return 'Teilnahmeurkunde';
		case 'KAENGURUSPRUNG': return 'Urkunde Kängurusprung';
		default: return 'Orkunde';
	}
}





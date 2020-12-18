export type Empfaengertyp = 'ALLE' | 'LEHRER' | 'PRIVATVERANSTALTER' | 'TEST';


export interface Newsletter {
	readonly uuid: string;
	readonly betreff: string;
	readonly text: string;
	readonly versandinfoIDs: string[];
};

export interface Versandingo {
	readonly uuid: string;
	readonly newsletterID: string;
	readonly empfaengertyp: Empfaengertyp;
	readonly anzahlAktuellVersendet: number;
	readonly anzahlEmpaenger: number;
	readonly versandBegonnenAm: string;
	readonly versandBeendetAm: string;
	readonly versandMitFehler: boolean;
};

export interface NewsletterVersandauftrag {
	readonly newsletterID: string;
	readonly empfaengertyp: Empfaengertyp;
};


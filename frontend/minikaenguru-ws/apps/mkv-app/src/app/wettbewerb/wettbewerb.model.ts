import { Teilnahme, TeilnahmeIdentifierAktuellerWettbewerb, WettbewerbStatus } from '@minikaenguru-ws/common-components';

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn: string;
	readonly wettbewerbsende: string;
	readonly datumFreischaltungLehrer: string;
	readonly datumFreischaltungPrivat: string;
};

export interface AbstractVeranstalter {
	readonly hatZugangZuUnterlagen: boolean;
	readonly newsletterAbonniert: boolean;
};

// tslint:disable-next-line:no-empty-interface
export interface Lehrer extends AbstractVeranstalter {
}

export interface Privatveranstalter extends AbstractVeranstalter {
	readonly anzahlTeilnahmen: number;
	readonly aktuellAngemeldet: boolean;
	readonly aktuelleTeilnahme?: Privatteilnahme;
	readonly teilnahmenummer: string;
}

// tslint:disable-next-line:no-empty-interface
export interface Privatteilnahme extends Teilnahme {	
}

export interface Schulteilnahme extends Teilnahme {
	readonly nameUrkunde: string;
	readonly angemeldetDurch: string;
};


export interface AnonymisierteTeilnahme extends Teilnahme {
	// hier kommt dann sowas wie Lösungszettelgruppen für jede Klassenstufe
	readonly nameSchule?: string;
};


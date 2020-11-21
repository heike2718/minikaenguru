import { Kind, Klasse, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';

export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn: string;
	readonly wettbewerbsende: string;
	readonly datumFreischaltungLehrer: string;
	readonly datumFreischaltungPrivat: string;
};

export interface AbstractVeranstalter {
	readonly hatZugangZuUnterlangen: boolean;
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

export interface AbstractTeilnahme {
	readonly identifier: TeilnahmeIdentifierAktuellerWettbewerb;
}

// tslint:disable-next-line:no-empty-interface
export interface Privatteilnahme extends AbstractTeilnahme {
}

export interface Schulteilnahme extends AbstractTeilnahme {
	readonly nameUrkunde: string;
	readonly angemeldetDurch: string;
};

export interface AnonymisierteTeilnahme extends AbstractTeilnahme {
	// hier kommt dann sowas wie Lösungszettelgruppen für jede Klassenstufe
	readonly nameSchule?: string;
};


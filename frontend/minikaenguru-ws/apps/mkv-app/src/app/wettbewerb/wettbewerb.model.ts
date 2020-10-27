import { TeilnahmeIdentifier, Kind, Klasse } from '@minikaenguru-ws/common-components';

export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn: string;
	readonly wettbewerbsende: string;
	readonly datumFreischaltungLehrer: string;
	readonly datumFreischaltungPrivat: string;
};

export interface Lehrer {
	readonly hatZugangZuUnterlangen: boolean;
	readonly newsletterAbonniert: boolean;
}

export interface Privatveranstalter {
	readonly hatZugangZuUnterlangen: boolean;
	readonly newsletterAbonniert: boolean;
	readonly anzahlTeilnahmen: number;
	readonly aktuellAngemeldet: boolean;
	readonly aktuelleTeilnahme?: Privatteilnahme;
	readonly teilnahmenummer: string;
}

export interface AbstractTeilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
}

// tslint:disable-next-line:no-empty-interface
export interface Privatteilnahme extends AbstractTeilnahme {
	// readonly kinderGeladen: boolean;
	// readonly kinder: Kind[];
}

export interface Schulteilnahme extends AbstractTeilnahme {
	readonly nameUrkunde: string;
	readonly anzahlKlassen: number;
	// readonly klassenGeladen: boolean;
	readonly angemeldetDurch: string;
	// readonly klassen: Klasse[];
};

export interface AnonymisierteTeilnahme extends AbstractTeilnahme {
	// hier kommt dann sowas wie Lösungszettelgruppen für jede Klassenstufe
	readonly nameSchule?: string;
};


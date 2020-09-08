import { Schule } from '../lehrer/schulen/schulen.model';

export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';
export type Teilnahmeart = 'PRIVAT' | 'SCHULE';
export type Klassenstufenart = 'IKID' | 'EINS' | 'ZWEI';
export type Sprachtyp = 'de' | 'en';


export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn: string;
	readonly wettbewerbsende: string;
	readonly datumFreischaltungLehrer: string;
	readonly datumFreischaltungPrivat: string;
};

export interface Privatveranstalter {
	readonly hatZugangZuUnterlangen: boolean;
	readonly anzahlVergangeneTeilnahmen: number;
	readonly aktuellAngemeldet: boolean;
	readonly aktuelleTeilnahme?: Privatteilnahme;
	readonly vergangeneTeilnahmenGeladen?: boolean;
	readonly vergangeneTeilnahmen: AnonymisierteTeilnahme[];
}

export interface Klassenstufe {
	readonly klassenstufe: Klassenstufenart;
	readonly label: string;
};

export interface Sprache {
	readonly sprache: Sprachtyp;
	readonly label: string;
};

export interface Kind {
	readonly vorname: string;
	readonly nachname?: string;
	readonly zusatz?: string;
	readonly klassenstufe: Klassenstufe;
	readonly sprache: Sprache;
};

export interface Auswertungsgruppe {
	readonly name: string;
	readonly klassenstufe: Klassenstufe;
	readonly kinder: Kind[];
}

export interface TeilnahmeIdentifier {
	readonly jahr: number;
	readonly teilnahmenummer: string;
	readonly teilnahmeart: Teilnahmeart;
}

export interface AbstractTeilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
}

export interface Privatteilnahme extends AbstractTeilnahme {
	readonly kinderGeladen: boolean;
	readonly kinder: Kind[];
}

export interface Schulteilnahme extends AbstractTeilnahme {
	readonly nameUrkunde: string;
	readonly anzahlKlassen: number;
	readonly klassenGeladen: boolean;
	readonly angemeldetDurch: string;
	readonly auswertungsgruppen: Auswertungsgruppe[];
};

export interface AnonymisierteTeilnahme extends AbstractTeilnahme {
	// hier kommt dann sowas wie Lösungszettelgruppen für jede Klassenstufe
	readonly nameSchule?: string;
};


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

export interface AbstractTeilnahme {
	readonly jahr: number;
	readonly teilnahmenummer: string;
	readonly teilnahmeart: Teilnahmeart;
	readonly anzahlKinder: number;
}

export interface Privatteilnahme extends AbstractTeilnahme {
	readonly kinderGeladen: boolean;
	readonly kinder: Kind[];
}

export interface AnonymisierteTeilnahme extends AbstractTeilnahme {
	// hier kommt dann sowas wie Lösungszettelgruppen für jede Klassenstufe
};


export type Teilnahmeart = 'PRIVAT' | 'SCHULE';
export type Klassenstufenart = 'IKID' | 'EINS' | 'ZWEI';
export type Sprachtyp = 'de' | 'en';

export interface TeilnahmeIdentifier {
	readonly jahr: number;
	readonly teilnahmenummer: string;
	readonly teilnahmeart: Teilnahmeart;
};

export interface Teilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
};

export interface Klassenstufe {
	readonly klassenstufe: Klassenstufenart;
	readonly label: string;
};

export interface Sprache {
	readonly sprache: Sprachtyp;
	readonly label: string;
};

export interface Klasse {
	readonly uuid: string;
	readonly name: string;
	readonly teilnahmeIdentifier: TeilnahmeIdentifier;
};

export interface Kind {
	readonly uuid: string;
	readonly vorname: string;
	readonly nachname?: string;
	readonly zusatz?: string;
	readonly klassenstufe: Klassenstufe;
	readonly sprache: Sprache;
	readonly loesungszettelId?: String;
};

export interface KindEditorModel {
	vorname: string;
	nachname: string;
	zusatz: string;
	klassenstufe: Klassenstufe,
	sprache: Sprache;
};


export const initialKindEditorModel: KindEditorModel = {
	vorname: '',
	nachname: '',
	zusatz: '',
	klassenstufe: undefined,
	sprache: {sprache: 'de', label: 'deutsch'}
};




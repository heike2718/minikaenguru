export type Teilnahmeart = 'PRIVAT' | 'SCHULE';
export type Klassenstufenart = 'IKID' | 'EINS' | 'ZWEI';
export type Sprachtyp = 'de' | 'en';
export type Duplikatkontext = 'KIND' | 'KLASSE';

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

export const KEINE_UUID = 'neu';

export const ALL_KLASSENSTUFEN: Klassenstufe[] = [
	{klassenstufe: 'EINS', label: 'Klasse 1'},
	{klassenstufe: 'ZWEI', label: 'Klasse 2'},
	{klassenstufe: 'IKID', label: 'Inklusion'}
];

export const ALL_SPRACHEN: Sprache[] = [
	{sprache: 'de', label: 'deutsch'},
	{sprache: 'en', label: 'englisch'}
];

export interface Duplikatwarnung {
	kontext: Duplikatkontext,
	warnungstext: string;
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

export interface PrivatkindRequestData {
	uuid: string,
	kind: KindEditorModel
};


export const initialKindEditorModel: KindEditorModel = {
	vorname: '',
	nachname: '',
	zusatz: '',
	klassenstufe: null,
	sprache: {sprache: 'de', label: 'deutsch'}
};

export function getKlassenstufeByLabel(label: string): Klassenstufe {

	for (let index = 0; index < ALL_KLASSENSTUFEN.length; index++) {
		const result = ALL_KLASSENSTUFEN[index];

		if (result.label === label) {
			return result;
		}
	}

	return undefined;
}

export function getSpracheByLabel(label: string): Sprache {

	for (let index = 0; index < ALL_SPRACHEN.length; index++) {
		const result = ALL_SPRACHEN[index];

		if (result.label === label) {
			return result;
		}
	}

	return undefined;
}




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
	{ klassenstufe: 'EINS', label: 'Klasse 1' },
	{ klassenstufe: 'ZWEI', label: 'Klasse 2' },
	{ klassenstufe: 'IKID', label: 'Inklusion' }
];

export const ALL_SPRACHEN: Sprache[] = [
	{ sprache: 'de', label: 'deutsch' },
	{ sprache: 'en', label: 'englisch' }
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
	sprache: { sprache: 'de', label: 'deutsch' }
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

function compareKlassenstufen(klassenstufe1: Klassenstufe, klassenstufe2: Klassenstufe): number {

	const indexKlassenstufe1 = ALL_KLASSENSTUFEN.findIndex(kl => kl.klassenstufe === klassenstufe1.klassenstufe);
	const indexKlassenstufe2 = ALL_KLASSENSTUFEN.findIndex(kl => kl.klassenstufe === klassenstufe2.klassenstufe);

	return indexKlassenstufe1 - indexKlassenstufe2;

}

function compareStrings(str1: string, str2: string): number {

	const result = str1 && str2 ? str1.localeCompare(str2) : 0;
	if (result !== 0) {
		return result;
	}
	if (!str1 && str2) {
		return -1;
	}
	if (str1 && !str2) {
		return 1;
	}
	return 0;
}

export function compareKinder(kind1: Kind, kind2: Kind): number {

	const vornameResult = kind1.vorname.localeCompare(kind2.vorname);

	if (vornameResult !== 0) {
		return vornameResult;
	}

	const nachnameResult = compareStrings(kind1.nachname, kind2.nachname);

	if (nachnameResult !== 0) {
		return nachnameResult;
	}

	const zusatzResult = compareStrings(kind1.zusatz, kind2.zusatz);

	if (zusatzResult !== 0) {
		return zusatzResult;
	}

	return compareKlassenstufen(kind1.klassenstufe, kind2.klassenstufe);
}


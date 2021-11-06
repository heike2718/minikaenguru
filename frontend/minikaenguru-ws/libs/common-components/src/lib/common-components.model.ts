import { NgbModalOptions } from "@ng-bootstrap/ng-bootstrap";

export type Teilnahmeart = 'PRIVAT' | 'SCHULE';
export type Klassenstufenart = 'IKID' | 'EINS' | 'ZWEI';
export type Sprachtyp = 'de' | 'en';
export type Duplikatkontext = 'KIND' | 'KLASSE';
export type ZulaessigeEingabe = 'A' | 'B' | 'C' | 'D' | 'E' | 'N';
export type ConcurrentModificationType = 'DETETED' | 'INSERTED' | 'UPDATED';
export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export const modalOptions: NgbModalOptions = {
    backdrop:'static',
    centered:true,
    ariaLabelledBy: 'modal-basic-title'
};

export interface TeilnahmeIdentifier {
	readonly jahr: number;
	readonly teilnahmenummer: string;
	readonly teilnahmeart: Teilnahmeart;
};

export interface TeilnahmeIdentifierAktuellerWettbewerb {
	readonly teilnahmenummer: string;
	readonly teilnahmeart: Teilnahmeart;
};

export interface Teilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
	readonly anzahlLoesungszettelOnline: number;
	readonly anzahlLoesungszettelUpload: number;
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

/** mapped LoesungszettelZeileAPIModel */
export interface Loesungszettelzeile {
	readonly index: number;
	readonly anzahlSpalten: number;
	readonly name: string;
	readonly eingabe: ZulaessigeEingabe;
};

/** mapped LoesungszettelpunkteAPIModel */
export interface LoesungszettelResponse {
	readonly loesungszettelId: string;
	readonly version: number;
	readonly concurrentModificationType?: string;
	readonly punkte: string;
	readonly laengeKaengurusprung: number;
	readonly zeilen: Loesungszettelzeile[];
};

export interface Kind {
	readonly uuid: string;
	readonly vorname: string;
	readonly nachname?: string;
	readonly zusatz?: string;
	readonly klassenstufe: Klassenstufe;
	readonly sprache: Sprache;
	readonly punkte?: LoesungszettelResponse;
	readonly klasseId?: string;
	readonly klassenstufePruefen: boolean;
	readonly dublettePruefen: boolean;
};

export interface KindEditorModel {
	vorname: string;
	nachname: string;
	zusatz: string;
	klassenstufe: Klassenstufe,
	sprache: Sprache,
	klasseUuid?: string;
};

export interface KindRequestData {
	uuid: string,
	klasseUuid?: string,
	kuerzelLand?: string,
	kind: KindEditorModel
};

export interface Klasse {
	readonly uuid: string;
	readonly name: string;
	readonly schulkuerzel: string;
	anzahlKinder: number;
	anzahlLoesungszettel: number;
};

export interface KlasseEditorModel {
	name: string;
};

export interface KlasseRequestData {
	uuid: string;
	schulkuerzel: string;
	klasse: KlasseEditorModel;
};

export interface UploadComponentModel {
	readonly subUrl: string;
	readonly titel: string;
	readonly maxSizeBytes: number;
	readonly errorMessageSize: string;
	readonly accept: string;
	readonly acceptMessage: string;
};

export const initialUploadComponentModel: UploadComponentModel = {
	subUrl: '',
	titel: '',
	maxSizeBytes: 0,
	errorMessageSize: '',
	accept: '',
	acceptMessage: ''	
};

export const initialKindEditorModel: KindEditorModel = {
	vorname: '',
	nachname: '',
	zusatz: '',
	klassenstufe: ALL_KLASSENSTUFEN[1],
	sprache: ALL_SPRACHEN[0],
};

export const initialKlasseEditorModel: KlasseEditorModel = {
	name: ''
};

export function getKlassenstufeByLabel(label: string): Klassenstufe {

	for (let index = 0; index < ALL_KLASSENSTUFEN.length; index++) {
		const result = ALL_KLASSENSTUFEN[index];

		if (result.label === label) {
			return result;
		}
	}

	return ALL_KLASSENSTUFEN[1];
}

export function getSpracheByLabel(label: string): Sprache {

	for (let index = 0; index < ALL_SPRACHEN.length; index++) {
		const result = ALL_SPRACHEN[index];

		if (result.label === label) {
			return result;
		}
	}

	return ALL_SPRACHEN[0];
}

function compareKlassenstufen(klassenstufe1?: Klassenstufe, klassenstufe2?: Klassenstufe): number {

	if (!klassenstufe1 && !klassenstufe2) {
		return 0;
	}

	if(!klassenstufe1 && klassenstufe2) {
		return -1;
	}
	if (klassenstufe1 && !klassenstufe2) {
		return 1;
	}

	if (klassenstufe1 && klassenstufe2) {

		const indexKlassenstufe1 = ALL_KLASSENSTUFEN.findIndex(kl => kl.klassenstufe === klassenstufe1.klassenstufe);
		const indexKlassenstufe2 = ALL_KLASSENSTUFEN.findIndex(kl => kl.klassenstufe === klassenstufe2.klassenstufe);

		return indexKlassenstufe1 - indexKlassenstufe2;

	}

	return 0;

}

function compareStrings(str1?: string, str2?: string): number {

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
};

export function compareKlassen(klasse1: Klasse, klasse2: Klasse): number {

	return klasse1.name.localeCompare(klasse2.name);

};

export function kindToString(kind: Kind): string {

	let result = kind.vorname;

	if (kind.nachname) {
		result = result + ' ' + kind.nachname;
	}
	if (kind.zusatz) {
		result = result + ' (' + kind.zusatz + ')'
	}

	return result;
};


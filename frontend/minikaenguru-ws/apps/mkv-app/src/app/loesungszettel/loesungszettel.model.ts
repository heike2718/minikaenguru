import { Klassenstufe, Sprache, Klassenstufenart } from '@minikaenguru-ws/common-components';

export type ZulaessigeEingabe = 'A' | 'B' | 'C' | 'D' | 'E' | 'N';

export interface Koordinaten {
	readonly row: number;
	readonly col: number;
};

export interface Antwortcheckbox {
	readonly koordinaten: Koordinaten;
	readonly checked: boolean;
};

export interface Antwortzeile {
	readonly index: number;
	readonly checkboxes: Antwortcheckbox[];
};


/** mapped LoesungszettelZeileAPIModel */
export interface Loesungszettelzeile {
	readonly index: number;
	readonly anzahlColumns: number;
	readonly eingabe: ZulaessigeEingabe;
};


/** mapped LoesungszettelAPIModel */
export interface Loesungszettel {
	readonly uuid: string;
	readonly kindID: string;
	readonly klassenstufe: Klassenstufenart;
	readonly zeilen: Loesungszettelzeile[];
};

export interface LoesungszettelWithID {
	readonly uuid: string;
	readonly loesungszettel: Loesungszettel;
};

// verpackt häufig erforderliche Operationen auf einem LoesungszettelWithID[] etwas handhabbarer.
export class LoesungszettelMap {

	private alleLoesungszettel: Map<string, Loesungszettel> = new Map();

	constructor(readonly items: LoesungszettelWithID[]) {


		if (items !== undefined) {
			for (const i of items) {
				this.alleLoesungszettel.set(i.uuid, i.loesungszettel);
			}
		}

	}

	public has(uuid: string): boolean {

		return this.alleLoesungszettel.has(uuid);
	}

	public get(uuid: string): Loesungszettel {

		if (!this.has(uuid)) {
			return null;
		}

		return this.alleLoesungszettel.get(uuid);
	}

	public toArray(): Loesungszettel[] {

		const array = [...this.alleLoesungszettel.values()];
		return array;
	}

	public merge(klasse: Loesungszettel): LoesungszettelWithID[] {

		const result: LoesungszettelWithID[] = [];

		if (!this.has(klasse.uuid)) {
			result.push({ uuid: klasse.uuid, loesungszettel: klasse });
		}
		for (const item of this.items) {
			if (item.uuid !== klasse.uuid) {
				result.push(item);
			} else {
				result.push({ uuid: klasse.uuid, loesungszettel: klasse });
			}
		}
		return result;
	}

	public remove(uuid: string): LoesungszettelWithID[] {

		const result: LoesungszettelWithID[] = [];

		for (const item of this.items) {
			if (item.uuid !== uuid) {
				result.push(item);
			}
		}

		return result;
	}
};


export function createLoseungszettelzeilen(klassenstufenart: Klassenstufenart): Loesungszettelzeile[] {

	const result: Loesungszettelzeile[] = [];
	const columns = klassenstufenart === 'IKID' ? 3 : 5;

	let zeilen: number;
	switch (klassenstufenart) {
		case 'IKID': zeilen = 6; break;
		case 'EINS': zeilen = 12; break;
		case 'ZWEI': zeilen = 15; break;
	}

	for (let index = 0; index < zeilen; index++) {
		result.push({ index: index, anzahlColumns: columns, eingabe: 'N' });
	}

	return result;
}

export function loseungszettelzeileToAntwortzeile(loesungszettelzeile: Loesungszettelzeile): Antwortzeile {

	if (loesungszettelzeile.anzahlColumns === 3) {

		switch (loesungszettelzeile.eingabe) {
			case 'A': return {
				index: loesungszettelzeile.index, checkboxes: [
					{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: true },
					{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
					{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false }]
			};
			case 'B': return {
				index: loesungszettelzeile.index, checkboxes: [
					{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: true },
					{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
					{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false }]
			};
			case 'C': return {
				index: loesungszettelzeile.index, checkboxes: [
					{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: true },
					{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
					{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false }]
			};
			default: return {
				index: loesungszettelzeile.index, checkboxes: [
					{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: true },
					{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
					{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false }]
			}
		}
	}

	switch (loesungszettelzeile.eingabe) {
		case 'A': return {
			index: loesungszettelzeile.index, checkboxes: [
				{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: true },
				{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 3 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 4 }, checked: false }]
		};
		case 'B': return {
			index: loesungszettelzeile.index, checkboxes: [
				{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: true },
				{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 3 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 4 }, checked: false }]
		};
		case 'C': return {
			index: loesungszettelzeile.index, checkboxes: [
				{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: true },
				{ koordinaten: { row: loesungszettelzeile.index, col: 3 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 4 }, checked: false }]
		};
		case 'D': return {
			index: loesungszettelzeile.index, checkboxes: [
				{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 3 }, checked: true },
				{ koordinaten: { row: loesungszettelzeile.index, col: 4 }, checked: false }]
		};
		case 'E': return {
			index: loesungszettelzeile.index, checkboxes: [
				{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 3 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 4 }, checked: true }]
		};
		case 'N': return {
			index: loesungszettelzeile.index, checkboxes: [
				{ koordinaten: { row: loesungszettelzeile.index, col: 0 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 1 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 2 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 3 }, checked: false },
				{ koordinaten: { row: loesungszettelzeile.index, col: 4 }, checked: false }]
		};
	}
};

export function antwortzeileToLoesungszettelzeile(antwortzeile: Antwortzeile): Loesungszettelzeile {

	const index = antwortzeile.index;
	const anzahlColumns = antwortzeile.checkboxes.length;

	for (let col = 0; col < anzahlColumns; col++) {
		const checkbox: Antwortcheckbox = antwortzeile.checkboxes[col];

		if (checkbox.checked) {

			switch (col) {
				case 0: return { index: index, anzahlColumns: anzahlColumns, eingabe: 'A' };
				case 1: return { index: index, anzahlColumns: anzahlColumns, eingabe: 'B' };
				case 2: return { index: index, anzahlColumns: anzahlColumns, eingabe: 'C' };
				case 3: {
					if (anzahlColumns === 3) {
						return { index: index, anzahlColumns: anzahlColumns, eingabe: 'N' };
					} else {
						return { index: index, anzahlColumns: anzahlColumns, eingabe: 'D' };
					}
				}
				case 4: {
					if (anzahlColumns === 3) {
						return { index: index, anzahlColumns: anzahlColumns, eingabe: 'N' };
					} else {
						return { index: index, anzahlColumns: anzahlColumns, eingabe: 'E' };
					}
				}
			}
		}
	}

	return { index: index, anzahlColumns: anzahlColumns, eingabe: 'N' };
}



import { Klassenstufe, Sprache, Klassenstufenart, LoesungszettelResponse, ZulaessigeEingabe, Loesungszettelzeile } from '@minikaenguru-ws/common-components';

export interface Koordinaten {
	readonly row: number;
	readonly col: number;
};

export interface CheckboxData {
	readonly rowIndex: number;
	readonly columnIndex: number;
	readonly checked: boolean;
};


/** mapped LoesungszettelAPIModel */
export interface Loesungszettel {
	readonly uuid: string;
	readonly version: number;
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

	public findWithKindUuid(kindUuid: string): Loesungszettel {

		for (const i of this.items) {

			if (i.loesungszettel.kindID === kindUuid) {
				return i.loesungszettel;
			}

		}


		return null;

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

	let zeilen: number;
	let anzahlSpalten = 5;
	switch (klassenstufenart) {
		case 'IKID': zeilen = 6; anzahlSpalten = 3; break;
		case 'EINS': zeilen = 12; break;
		case 'ZWEI': zeilen = 15; break;
	}

	for (let index = 0; index < zeilen; index++) {

		switch (klassenstufenart) {
			case 'IKID': {

				switch (index) {
					case 0: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-1', eingabe: 'N' }); break;
					case 1: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-2', eingabe: 'N' }); break;

					case 2: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-1', eingabe: 'N' }); break;
					case 3: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-2', eingabe: 'N' }); break;

					case 4: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-1', eingabe: 'N' }); break;
					case 5: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-2', eingabe: 'N' }); break;
				}
			}
				break;
			case 'EINS': {
				switch (index) {
					case 0: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-1', eingabe: 'N' }); break;
					case 1: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-2', eingabe: 'N' }); break;
					case 2: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-3', eingabe: 'N' }); break;
					case 3: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-4', eingabe: 'N' }); break;

					case 4: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-1', eingabe: 'N' }); break;
					case 5: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-2', eingabe: 'N' }); break;
					case 6: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-3', eingabe: 'N' }); break;
					case 7: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-4', eingabe: 'N' }); break;

					case 8: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-1', eingabe: 'N' }); break;
					case 9: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-2', eingabe: 'N' }); break;
					case 10: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-3', eingabe: 'N' }); break;
					case 11: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-4', eingabe: 'N' }); break;
				}
			}
				break;
			case 'ZWEI': {

				switch (index) {
					case 0: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-1', eingabe: 'N' }); break;
					case 1: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-2', eingabe: 'N' }); break;
					case 2: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-3', eingabe: 'N' }); break;
					case 3: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-4', eingabe: 'N' }); break;
					case 4: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'A-5', eingabe: 'N' }); break;

					case 5: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-1', eingabe: 'N' }); break;
					case 6: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-2', eingabe: 'N' }); break;
					case 7: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-3', eingabe: 'N' }); break;
					case 8: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-4', eingabe: 'N' }); break;
					case 9: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'B-5', eingabe: 'N' }); break;

					case 10: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-1', eingabe: 'N' }); break;
					case 11: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-2', eingabe: 'N' }); break;
					case 12: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-3', eingabe: 'N' }); break;
					case 13: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-4', eingabe: 'N' }); break;
					case 14: result.push({ index: index, anzahlSpalten: anzahlSpalten, name: 'C-5', eingabe: 'N' }); break;
				}

			}
				break;
		}




	}

	return result;
};

export function loesungszettelzeileNichtLeer(zeile: Loesungszettelzeile): boolean {

	if (!zeile) {
		return false;
	}

	return zeile.eingabe !== 'N';

}

export function loesungszettelIsLeer(loesungszettel: Loesungszettel): boolean {

	if (!loesungszettel) {
		return true;
	}

	for (let i = 0; i < loesungszettel.zeilen.length; i++) {

		if (loesungszettelzeileNichtLeer(loesungszettel.zeilen[i])) {
			return false;
		}
	}

	return true;
}




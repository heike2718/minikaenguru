import { Klasse, compareKlassen } from '@minikaenguru-ws/common-components';

export interface KinderKlasseLocalStorageModel {
	readonly schulkuerzel: string;
	readonly klasseUuid: string
};


export interface KlasseWithID {
	readonly uuid: string;
	readonly klasse: Klasse
};

export interface KlassenlisteImportReport {
	readonly anzahlKlassen: number;
	readonly anzahlKinderImportiert: number;
	readonly anzahlNichtImportiert: number;
	readonly anzahlKlassenstufeUnklar: number;
	readonly anzahlDubletten: number;
	readonly nichtImportierteZeilen: string[];
	readonly klassen: Klasse[];
};


// verpackt häufig erforderliche Operationen auf einem KlasseWithID[] etwas handhabbarer.
export class KlassenMap {

	private klassen: Map<string, Klasse> = new Map();

	constructor(readonly items: KlasseWithID[]) {

		if (items !== undefined) {
			for (const i of items) {
				this.klassen.set(i.uuid, i.klasse);
			}
		}

	}

	public has(uuid: string): boolean {

		return this.klassen.has(uuid);
	}

	public get(uuid: string): Klasse | undefined {

		if (!this.has(uuid)) {
			return undefined;
		}

		return this.klassen.get(uuid);
	}

	public toArray(): Klasse[] {

		const array = [...this.klassen.values()];
		array.sort((klasse1, klasse2) => compareKlassen(klasse1, klasse2));
		return array;
	}

	public merge(klasse: Klasse): KlasseWithID[] {

		const result: KlasseWithID[] = [];

		if (!this.has(klasse.uuid)) {
			result.push({ uuid: klasse.uuid, klasse: klasse });
		}
		for (const item of this.items) {
			if (item.uuid !== klasse.uuid) {
				result.push(item);
			} else {
				result.push({ uuid: klasse.uuid, klasse: klasse });
			}
		}
		return result;
	}

	public remove(uuid: string): KlasseWithID[] {

		const result: KlasseWithID[] = [];

		for (const item of this.items) {
			if (item.uuid !== uuid) {
				result.push(item);
			}
		}

		return result;
	}

	public containsName(param: {uuid?: string, name?: string}): boolean {

		if (!param.name || !param.uuid) {
			return false;
		}

		for (const item of this.items) {

			if (item.uuid !== param.uuid && item.klasse.name.toLocaleLowerCase() === param.name.trim().toLocaleLowerCase()) {
				return true;
			}

		}
		return false;
	}

	public countLoesungszettel(): number {

		let count = 0;

		for (const item of this.items) {

			count+= item.klasse.anzahlLoesungszettel;
		}

		return count;

	}
};

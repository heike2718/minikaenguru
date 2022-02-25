import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';

export interface Schule {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
	readonly kuerzelLand: string,
	readonly aktuellAngemeldet: boolean;
	readonly details?: SchuleDetails;
};

export interface SchuleDetails {
	readonly kuerzel: string;
	readonly kollegen?: string; // kommaseparierte fullName
	readonly angemeldetDurch?: string; // fullName
	readonly anzahlTeilnahmen: number;
	readonly vergangeneTeilnahmenGeladen?: boolean;
	readonly vergangeneTeilnahmen?: AnonymisierteTeilnahme[];
	readonly hatAdv: boolean;
	readonly offlineauswertungBegonnen: boolean;
	readonly onlineauswertungBegonnen: boolean;
};

export interface SchuleWithID {
	readonly kuerzel: string;
	readonly schule: Schule;
}

export interface Person {
	readonly fullName: string;
};


export interface SchulanmeldungRequestPayload {
	schulkuerzel: string;
	schulname: string;
};

export function indexOfSchuleMitId(schulenWithID: SchuleWithID[], kuerzel: string): number {

	if (!schulenWithID) {
		return -1;
	}

	for (let ind: number = 0; ind < schulenWithID.length; ind++) {
		if (schulenWithID[ind] && schulenWithID[ind].kuerzel === kuerzel) {
			return ind;
		}
	}

	return -1;
}

export function findSchuleMitId(schulenWithID: SchuleWithID[], kuerzel: string): Schule | undefined {

	if (!schulenWithID) {
		return undefined;
	}

	if (!kuerzel) {
		return undefined;
	}

	const index = indexOfSchuleMitId(schulenWithID, kuerzel);

	if (index >= 0) {
		return schulenWithID[index].schule;
	}

	return undefined;
}

export function mergeSchulenMap(schulenMap: SchuleWithID[], schule: Schule): SchuleWithID[] {

	const result: SchuleWithID[] = [];

	if (!schulenMap) {
		return result;
	}

	for (let i = 0; i < schulenMap.length; i++) {
		const schuleMitId: SchuleWithID = schulenMap[i];

		if (schuleMitId.kuerzel !== schule.kuerzel) {
			result.push(schuleMitId);
		} else {
			result.push({ kuerzel: schule.kuerzel, schule: schule })
		}
	}

	return result;
}

// verpackt häufig erforderliche Operationen auf einem SchuleWithID[] etwas handhabbarer.
export class SchulenMap {

	private schulen: Map<string, Schule> = new Map();

	constructor(readonly items: SchuleWithID[]) {
		if (items !== undefined) {
			for (const k of items) {
				this.schulen.set(k.kuerzel, k.schule);
			}
		}
	}

	public has(kuerzel: string): boolean {

		return this.schulen.has(kuerzel);
	}

	public get(kuerzel: string): Schule | undefined{

		if (!this.has(kuerzel)) {
			return undefined;
		}

		return this.schulen.get(kuerzel);
	}

	public toArray(): Schule[] {

		const array = [...this.schulen.values()];
		array.sort((schule1, schule2) => compareSchulen(schule1, schule2));
		return array;
	}

	public filterWithKlasse(schule: Schule): Schule[] {

		if (!schule) {
			return this.toArray();
		}

		const array = [...this.schulen.values()];
		const filtered = array.filter((s: Schule) => s.kuerzel === schule.kuerzel);
		filtered.sort((schule1, schule2) => compareSchulen(schule1, schule2));
		return filtered;
	}

	public merge(schule: Schule): SchuleWithID[] {

		const result: SchuleWithID[] = [];

		if (!this.has(schule.kuerzel)) {
			result.push({ kuerzel: schule.kuerzel, schule: schule });
		}
		for (const item of this.items) {
			if (item.kuerzel !== schule.kuerzel) {
				result.push(item);
			} else {
				result.push({ kuerzel: schule.kuerzel, schule: schule });
			}
		}

		result.sort((s1, s2) => this.compareSchulenWithID(s1, s2));
		return result;
	}

	public remove(uuid: string): SchuleWithID[] {

		const result: SchuleWithID[] = [];

		for (const item of this.items) {
			if (item.kuerzel !== uuid) {
				result.push(item);
			}
		}

		result.sort((s1, s2) => this.compareSchulenWithID(s1, s2));
		return result;
	}

	private compareSchulenWithID(schule1: SchuleWithID, schule2: SchuleWithID): number {

		return schule1.schule.name.localeCompare(schule2.schule.name);

	}
};

export function compareSchulen(schule1: Schule, schule2: Schule): number {

	return schule1.name.localeCompare(schule2.name);

};



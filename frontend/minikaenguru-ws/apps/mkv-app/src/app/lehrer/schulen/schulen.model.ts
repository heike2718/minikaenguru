import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';

export interface Schule {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
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

export function findSchuleMitId(schulenWithID: SchuleWithID[], kuerzel: string): Schule {

	if (!schulenWithID) {
		return null;
	}

	if (!kuerzel) {
		return null;
	}

	const index = indexOfSchuleMitId(schulenWithID, kuerzel);

	if (index >= 0) {
		return schulenWithID[index].schule;
	}

	return null;
}

export function mergeSchulenMap(schulenMap: SchuleWithID[], schule: Schule): SchuleWithID[] {

	const result: SchuleWithID[] = [];

	if (!schulenMap) {
		return result;
	}

	for (let i: number = 0; i < schulenMap.length; i++) {
		const schuleMitId: SchuleWithID = schulenMap[i];

		if (schuleMitId.kuerzel !== schule.kuerzel) {
			result.push(schuleMitId);
		} else {
			result.push({ kuerzel: schule.kuerzel, schule: schule })
		}
	}

	return result;
}




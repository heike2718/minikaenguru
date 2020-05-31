
export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Teilnahmenuebersicht {
	readonly anzahlSchulanmeldungen: number;
	readonly anzahlPrivatanmeldungen: number;
	readonly anzahlLoesungszettelSchulen: number;
	readonly anzahlLoesungszettelPrivat: number;
};

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn?: string;
	readonly wettbewerbsende?: string;
	readonly datumFreischaltungLehrer?: string;
	readonly datumFreischaltungPrivat?: string;
	readonly teilnahmenuebersicht?: Teilnahmenuebersicht;
};

export interface WettbewerbWithID {
	jahr: number;
	wettbewerb: Wettbewerb;
}

export interface WettbewerbEditorModel {
	jahr: number,
	status: WettbewerbStatus,
	wettbewerbsbeginn: string
	wettbewerbsende: string;
	datumFreischaltungLehrer: string;
	datumFreischaltungPrivat: string;
};

export const initialWettbewerbEditorModel: WettbewerbEditorModel = {
	jahr: 0,
	status: 'ERFASST',
	wettbewerbsbeginn: '11.11.2013',
	wettbewerbsende: '01.08.2014',
	datumFreischaltungLehrer: '05.03.2014',
	datumFreischaltungPrivat: '01.06.2014'
}

export function wettbewerbeWithIDArrayToWettbewerbeArray(wettbewerbeMitID: WettbewerbWithID[]): Wettbewerb[] {

	const result: Wettbewerb[] = [];
	wettbewerbeMitID.forEach(wmi => result.push(wmi.wettbewerb));
	return result;

}

export function indexOfWettbewerbMitId(wettbewerbeMitID: WettbewerbWithID[], jahr: number): number {

	if (!wettbewerbeMitID) {
		return -1;
	}

	for(let ind: number = 0; wettbewerbeMitID.length; ind++) {
		if (wettbewerbeMitID[ind] && wettbewerbeMitID[ind].jahr === jahr) {
			return ind;
		}
	}

	return -1;

}

export function findWettbewerbMitId(wettbewerbeMitID: WettbewerbWithID[], jahr: number): Wettbewerb {

	if (wettbewerbeMitID === undefined) {
		return null;
	}

	if (jahr === NaN) {
		return null;
	}

	const index = indexOfWettbewerbMitId(wettbewerbeMitID, jahr);

	if (index >= 0) {
		return wettbewerbeMitID[index].wettbewerb;
	}

	return null;
}


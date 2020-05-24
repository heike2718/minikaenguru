
export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Teilnahmenuebersicht {
	readonly anzahlSchulanmeldungen: number;
	readonly anzahlPrivatanmeldungen: number;
	readonly anzahlLoesungszettelSchulen: number;
	readonly anzahlLoesungszettelPrivat: number;
}

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn?: Date;
	readonly wettbewerbsende?: Date;
	readonly datumFreischaltungLehrer?: Date;
	readonly datumFreischaltungPrivat?: Date;
	readonly teilnahmenuebersicht?: Teilnahmenuebersicht;
}

export interface WettbewerbWithID {
	jahr: number;
	wettbewerb: Wettbewerb;
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

	if (wettbewerbeMitID === undefined || !jahr) {
		return null;
	}

	const index = indexOfWettbewerbMitId(wettbewerbeMitID, jahr);

	if (index >= 0) {
		return wettbewerbeMitID[index].wettbewerb;
	}

	return null;
}


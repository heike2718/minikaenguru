import { wettbewerbMovedOn } from './+state/wettbewerbe.actions';

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
	readonly loesungsbuchstabenIkids: string;
	readonly loesungsbuchstabenKlasse1: string;
	readonly loesungsbuchstabenKlasse2: string;
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
	loesungsbuchstabenIkids: string;
	loesungsbuchstabenKlasse1: string;
	loesungsbuchstabenKlasse2: string;
};

export const initialWettbewerbEditorModel: WettbewerbEditorModel = {
	jahr: 0,
	status: 'ERFASST',
	wettbewerbsbeginn: '',
	wettbewerbsende: '',
	datumFreischaltungLehrer: '',
	datumFreischaltungPrivat: '',
	loesungsbuchstabenIkids: '',
	loesungsbuchstabenKlasse1: '',
	loesungsbuchstabenKlasse2: ''
}

export function wettbewerbeWithIDArrayToWettbewerbeArray(wettbewerbeMitID: WettbewerbWithID[]): Wettbewerb[] {

	const result: Wettbewerb[] = [];
	wettbewerbeMitID.forEach(wmi => result.push(wmi.wettbewerb));
	return result;

}

export function findWettbewerbMitId(wettbewerbeMitID: WettbewerbWithID[], jahr: number): Wettbewerb {


	if (wettbewerbeMitID === undefined) {
		return null;
	}

	if (jahr === NaN) {
		return null;
	}

	const realMap = transformToMap(wettbewerbeMitID);

	if (realMap.has(jahr)) {
		return realMap.get(jahr).wettbewerb;
	}

	return null;
}

export function mergeWettbewerbeMap(wettbewerbeMap: WettbewerbWithID[], wettbewerb: Wettbewerb): WettbewerbWithID[] {

	const result: WettbewerbWithID[] = [];

	for (const wbMitId of wettbewerbeMap) {
		if (wbMitId.jahr !== wettbewerb.jahr) {
			result.push(wbMitId);
		} else {
			result.push({ jahr: wettbewerb.jahr, wettbewerb: wettbewerb });
		}
	}
	return result;
}

/////// private functions

function transformToMap(wettbewerbeMap: WettbewerbWithID[]): Map<number, WettbewerbWithID> {

	const result = new Map();

	for (const element of wettbewerbeMap) {
		result.set(element.jahr, element);
	}
	return result;
}





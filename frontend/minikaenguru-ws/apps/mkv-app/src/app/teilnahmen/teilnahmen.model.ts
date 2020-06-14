export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export type ZugangUnterlagenStatus = 'DEFAULT' | 'ERTEILT' | 'ENTZOGEN';

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn: string;
	readonly wettbewerbsende: string;
	readonly datumFreischaltungLehrer: string;
	readonly datumFreischaltungPrivat: string;
};

export function hatZugangZuUnterlagen(wettbewerbStatus: WettbewerbStatus, zugangStatus: ZugangUnterlagenStatus): boolean {

	return false;


}


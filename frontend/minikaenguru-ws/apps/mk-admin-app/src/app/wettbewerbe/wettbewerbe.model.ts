
export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Wettbewerb {
	readonly jahr: number;
	readonly status: WettbewerbStatus;
	readonly wettbewerbsbeginn: Date;
	readonly wettbewerbsende: Date;
	readonly datumFreischaltungLehrer: Date;
	readonly datumFreischaltungPrivat: Date;
}

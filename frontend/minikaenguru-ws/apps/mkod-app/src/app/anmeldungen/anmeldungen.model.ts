export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Anmeldungsitem {
	readonly name: string;
	readonly anzahlAnmeldungen: number;
	readonly anzahlLoesungszettel: number;
};

export interface Anmeldungen {
	readonly wettbewerbsjahr: string;
	readonly statusWettbewerb: WettbewerbStatus;
	readonly privatanmeldungen: Anmeldungsitem;
	readonly schulanmeldungen: Anmeldungsitem;
	readonly laender: Anmeldungsitem[];
};

export interface AnmeldungLandWithID {
	readonly name: string;
	readonly land: Anmeldungsitem;

};


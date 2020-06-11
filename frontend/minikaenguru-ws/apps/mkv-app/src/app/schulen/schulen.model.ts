export interface Schule {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
	readonly aktuellAngemeldet : boolean;
}

export interface SchuleDashboardModel {
	readonly kuerzel: string;
	readonly nameUrkunde?: string;
	readonly kollegen?: string; // kommaseparierte fullName
	readonly angemeldetDurch?: string; // fullName
	readonly anzahlTeilnahmen?: number;
}

export interface Person {
	readonly fullName: string;
}


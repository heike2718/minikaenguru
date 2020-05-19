export interface Schule {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
	readonly aktuellAngemeldet : boolean;
	readonly dashboardModel?: SchuleDashboardModel
}

export interface SchuleDashboardModel {
	readonly kollegen?: Person[]; // erfordelich für schule-dashboard.component
	readonly angemeldetDurch?: Person; // erfordelich für schule-dashboard.component
	readonly anzahlTeilnahmen?: number; // erfordelich für schule-dashboard.component
}

export interface Person {
	readonly fullName: string;
}


// TODO pipe basteln!!!!
export function kollegenAsString(kollegen: Person[]): string {

	let result = '';
	for(let i = 0; i < kollegen.length; i++ ) {
		result += kollegen[i].fullName;
		if (i < kollegen.length -1 ) {
			result += ', ';
		}
	}

	return result;

}



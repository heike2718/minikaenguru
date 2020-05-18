export interface Schule {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
	readonly kollegium: Person[];
	readonly aktuellAngemeldet : boolean;
	readonly angemeldetDurch?: string;
}

export interface Person {
	readonly fullName: string;
}


export function getAndereKollegenAsString(kollegium: Person[], fullName: string): string {

	const andere = kollegium.filter((person, _index, _array) => person.fullName !== fullName);

	let result = '';
	for(let i = 0; i < andere.length; i++ ) {
		result += andere[i].fullName;
		if (i < andere.length -1 ) {
			result += ', ';
		}
	}

	return result;

}



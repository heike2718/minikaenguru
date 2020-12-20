import { Klassenstufe, Sprache } from '@minikaenguru-ws/common-components';

export type ZulaessigeEingabe = 'A' | 'B' | 'C' | 'D' | 'E' | 'N';

export interface Antwortbuchstabe {
	readonly eingabe: ZulaessigeEingabe;
	readonly colIndex: number;
	readonly rowIndex: number;
};

export interface Antwortzeile {
	readonly eingaben: ZulaessigeEingabe[];
	readonly index: number;
};

export interface Koordinaten {
	readonly row: number;
	readonly col: number;
}

export interface Antwortcheckbox {
	readonly koordinaten: Koordinaten;
	readonly checked: boolean;
	readonly color: string;
};

export interface MatrixzeileID {
	readonly loesungszettelID: string;
	readonly row: number;
};

export interface Matrixzeile {
	readonly id: MatrixzeileID;
	readonly zellen: Antwortcheckbox[];
};

export interface Loesungszettelmatrix {
	readonly uuid: string;
	readonly kindID: string;
	readonly zeilen: Matrixzeile[];
};



export interface Loesungszettel {
	readonly uuid: string;
	readonly kindID: string;
	readonly klassenstufe: Klassenstufe;
	readonly sprache: Sprache;
}




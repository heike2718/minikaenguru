import { Klassenstufenart, Sprachtyp, Teilnahmeart } from "@minikaenguru-ws/common-components";

export type QUELLE = 'UPLOAD' | 'ONLINE';

export interface Schule {
    readonly name: string;
	readonly kuerzelOrt: string;
	readonly nameOrt: string;
	readonly kuerzelLand: string;
	readonly nameLand: string;
};

export interface Loesungszettel {
    readonly uuid: string;
    readonly sortnumber: number;
    readonly teilnahmeart: Teilnahmeart;
    readonly teilnahmenummer: string;    
    readonly quelle: QUELLE;
    readonly klassenstufe: Klassenstufenart;
    readonly sprache: Sprachtyp;
    readonly punkte: string;
    readonly kaengurusprung: number;
    readonly schule?: Schule;
    readonly nutzereingabe: string;
    readonly antwortcode?: string;
    readonly wertungscode: string;
    readonly kindUuid?: string;
};


export interface LoesungszettelWithID {
    readonly uuid: string;
    readonly loesungszettel: Loesungszettel;
};

export class LoesungszettelMap {

    private alleLoesungszettel: Map<string, Loesungszettel> = new Map();

    constructor(private items: LoesungszettelWithID[]) {

        for (const item of items) {
            this.alleLoesungszettel.set(item.uuid, item.loesungszettel);
        }
    }

    public merge(loesungszettel: Loesungszettel[]): LoesungszettelWithID[] {

        const result: LoesungszettelWithID[] = [];

        if (loesungszettel.length === 0) {
            return this.add(loesungszettel);
        }

        const itemsSorted: LoesungszettelWithID[] = this.items.sort((item1, item2) => item1.loesungszettel.sortnumber - item2.loesungszettel.sortnumber);

        for (let l of loesungszettel) {

            for (let item of itemsSorted) {
                if (item.uuid !== l.uuid) {
                    result.push(item);
                } else {
                    result.push({uuid: l.uuid, loesungszettel: l});
                }
            }
        }

        return result;
    }

    public add(loesungszettel: Loesungszettel[]): LoesungszettelWithID[] {

		const result: LoesungszettelWithID[] = [...this.items];

		if (loesungszettel.length === 0) {
			return result;
		}

		for (const l of loesungszettel) {
			result.push({uuid: l.uuid, loesungszettel: l});
		}

		return result;
	}

};

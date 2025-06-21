import { Klassenstufenart, Sprachtyp, Teilnahmeart } from "@minikaenguru-ws/common-components";

export type QUELLE = 'UPLOAD' | 'ONLINE';

export interface Schule {
    readonly name: string;
	readonly kuerzelOrt: string;
	readonly nameOrt: string;
	readonly kuerzelLand: string;
	readonly nameLand: string;
};

export interface LoesungszettelRohdaten {
    readonly nutzereingabe: string;
    readonly antwortcode?: string;
    readonly wertungscode: string;
}

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
    readonly kindUuid?: string;
    readonly rohdaten: LoesungszettelRohdaten;
};


export interface LoesungszettelWithID {
    readonly uuid: string;
    readonly loesungszettel: Loesungszettel;
};

export interface LoesungszettelPage {
    readonly pageNumber: number;
    readonly content: Loesungszettel[];
};

export class LoesungszettelPageMap {

    private pages: Map<number, Loesungszettel[]> = new Map();

    constructor (private items: LoesungszettelPage[]) {
        
        for (const item of items ) {
            this.pages.set(item.pageNumber, item.content);
        }
    }

    public has(pageNumber: number): boolean {

        return this.pages.has(pageNumber);
    }

    public getContent(pageNumber: number): Loesungszettel[] {

        if (!this.has(pageNumber)) {
            return [];
        }

        return this.pages.get(pageNumber)!;
    }

    public merge(loesungszettelPage: LoesungszettelPage): LoesungszettelPage[] {

        const result : LoesungszettelPage[] = [];

        if (!this.has(loesungszettelPage.pageNumber)) {
            result.push(loesungszettelPage);            
        }

        for (let p of this.items) {

            if (p.pageNumber !== loesungszettelPage.pageNumber) {
                result.push(p);
            } else {
                result.push(loesungszettelPage);
            }
        }

        return result;
    }
};

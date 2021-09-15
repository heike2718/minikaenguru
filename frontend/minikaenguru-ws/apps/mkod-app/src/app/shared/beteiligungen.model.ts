import { Klassenstufenart } from "@minikaenguru-ws/common-components";
import { anmeldungenState } from "../anmeldungen/+state/anmeldungen.selectors";

export type WettbewerbStatus = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_PRIVAT' | 'DOWNLOAD_LEHRER' | 'BEENDET';

export interface Median {

	readonly klassenstufe: Klassenstufenart;
	readonly median: string;
	readonly anzahlLoesungszettel:number;

};

export interface Anmeldungsitem {
	readonly name: string;
	readonly anzahlAnmeldungen: number;
	readonly anzahlLoesungszettel: number;
	readonly mediane: Median[];
};

export interface Anmeldungen {
	readonly wettbewerbsjahr: string;
	readonly statusWettbewerb: WettbewerbStatus;
	readonly privatanmeldungen: Anmeldungsitem;
	readonly schulanmeldungen: Anmeldungsitem;
	readonly laender: Anmeldungsitem[];
	readonly mediane: Median[];
};

export interface AnmeldungLandWithID {
	readonly name: string;
	readonly land: Anmeldungsitem;

};

export interface AnmeldungenWithID {
	readonly wettbewerbsjahr: string;
	readonly anmeldunegn: Anmeldungen;
}


// verpackt häufig erforderliche Operationen auf einem WettbewerbWithID[] etwas handhabbarer.
export class AnmeldungenMap {

	private anmeldungen: Map<string, Anmeldungen> = new Map();

	constructor(readonly items: AnmeldungenWithID[]) {
		if (items !== undefined) {
			for (const wb of items) {
				this.anmeldungen.set(wb.wettbewerbsjahr, wb.anmeldunegn);
			}
		}
	}

	public has(jahr: string): boolean {

		return this.anmeldungen.has(jahr);
	}

	public get(jahr: string): Anmeldungen {

		if (!this.has(jahr)) {
			return null;
		}

		return this.anmeldungen.get(jahr);
	}

	public toArray(): Anmeldungen[] {

		return [...this.anmeldungen.values()];
	}

	public merge(anmeldungen: Anmeldungen): AnmeldungenWithID[] {

		const result: AnmeldungenWithID[] = [];
		if (!this.has(anmeldungen.wettbewerbsjahr)) {
			result.push({wettbewerbsjahr: anmeldungen.wettbewerbsjahr, anmeldunegn: anmeldungen});
		}

		for (const item of this.items) {

			if (item.wettbewerbsjahr !== anmeldungen.wettbewerbsjahr) {
				result.push(item);
			} else {
				result.push({wettbewerbsjahr: anmeldungen.wettbewerbsjahr, anmeldunegn: anmeldungen});
			}

		}

		return result;
	}
}


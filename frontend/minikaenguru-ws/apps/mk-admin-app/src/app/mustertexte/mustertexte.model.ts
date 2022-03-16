import { Mustertext, MUSTRETEXT_KATEGORIE } from "../shared/shared-entities.model";



export interface MustertextWithID {
	readonly uuid: string;
	readonly mustertext: Mustertext;
};

export class MustertexteMap {

    private mustertexte : Map<string, Mustertext> = new Map();

    constructor(private readonly items: MustertextWithID[]) {

        if (items !== undefined) {
			for (const nl of items) {
				this.mustertexte.set(nl.uuid, nl.mustertext);
			}
		}
    }

    public has(uuid: string): boolean {

		if (!uuid) {
			return false;
		}

		return this.mustertexte.has(uuid);
	}


    public get(uuid: string): Mustertext | undefined {

		if (!this.has(uuid)) {
			return undefined;
		}

		return this.mustertexte.get(uuid);
	}

    public toArray(): Mustertext[] {

		return [...this.mustertexte.values()];
	}

	public filterByKategorie(kategorie: MUSTRETEXT_KATEGORIE): MustertextWithID[] {

		const result: MustertextWithID[] = [];

		if (kategorie === 'UNDEFINED') {
			for(const item of this.items) {
				result.push(item);
			}
		} else {

			for(const item of this.items) {

				if (kategorie === item.mustertext.kategorie) {
					result.push(item);
				}
			}
		}

		return result;
	}

    public merge(mustertext: Mustertext): MustertextWithID[] {

		const result: MustertextWithID[] = [];

		if (!this.has(mustertext.uuid)) {
			result.push({uuid: mustertext.uuid, mustertext: mustertext});
		}

		for(const item of this.items) {
			if (item.uuid !== mustertext.uuid) {
				result.push(item);
			} else {
				result.push({uuid: mustertext.uuid, mustertext: mustertext});
			}
		}

		return result;
	}

	public remove(mustertext: Mustertext): MustertextWithID[] {

		const result: MustertextWithID[] = [];

		for(const item of this.items) {
			if (item.uuid !== mustertext.uuid) {
				result.push(item);
			}
		}

		return result;
	}
};


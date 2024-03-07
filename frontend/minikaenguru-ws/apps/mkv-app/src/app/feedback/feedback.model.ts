import { Klassenstufenart } from "@minikaenguru-ws/common-components";

export type Aufgabenkategorie = 'LEICHT' | 'MITTEL ' | 'SCHWER';

export interface Images {
    readonly imageFrage: string;
    readonly imageLoesung: string;
};

export interface Aufgabe {
    readonly nummer: string;
    readonly punkte: number;
    readonly loesungsbuchstabe: string;
    readonly quelle: string;
    readonly images: Images;
};

export interface Aufgabenvorschau {
    readonly klassenstufe: string;
    readonly aufgaben: Aufgabe[];
};

export interface BewertungAufgabe {
    readonly nummer: string;
    readonly scoreSchwierigkeitsgrad: number;
    readonly empfohleneKategorie?: Aufgabenkategorie;
    readonly scoreLehrplankompatibilitaet: number;
    readonly scoreVerstaendlichkeit: number;
    readonly freitext: string;
};

export interface BewertungsbogenKlassenstufe {
    readonly klassenstufe: Klassenstufenart;
    readonly scoreSpassfaktor: number;
    readonly scoreZufriedenheit: number;
    readonly bewertungenAufgaben: BewertungAufgabe[];
    readonly freitext: string;
};

export interface BewertungAufgabeGUIModel {
    readonly bewertungAufgabe: BewertungAufgabe;
    readonly vorschau: Aufgabe;
};

export interface BewertungsbogenGUIModel {
    readonly klassenstufe: string;
    readonly bewertungsbogen: BewertungsbogenKlassenstufe;
    readonly items: BewertungAufgabeGUIModel[]
};

export function createBewertungAufgabe(aufgabe: Aufgabe): BewertungAufgabe {

    return {
        empfohleneKategorie: undefined,
        freitext: '',
        nummer: aufgabe.nummer,
        scoreLehrplankompatibilitaet: 0,
        scoreSchwierigkeitsgrad: 0,
        scoreVerstaendlichkeit: 0
    };

}

export function createBewertungAufgabeGUIModel(aufgabe: Aufgabe): BewertungAufgabeGUIModel {

    return {
        bewertungAufgabe: createBewertungAufgabe(aufgabe),
        vorschau: aufgabe
    };
}

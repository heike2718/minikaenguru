export type StatusWettbewerb = 'ERFASST' | 'ANMELDUNG' | 'DOWNLOAD_LEHRER' | 'DOWNLOAD_PRIVAT' | 'BEENDET';
export type Klassenstufe = 'IKID' | 'EINS' | 'ZWEI';

export const BAR_BACKGROUND_COLOR_BLUE = 'rgba(54, 162, 235, 0.5)';

export interface Images {
  readonly imageFrage: string,
  readonly imageLoesung: string
};

export interface Gruppierungsitem {
  readonly name: string,
  readonly anzahl: number
};

export interface WettbewerbOverview {
  readonly jahr: number,
  readonly status: StatusWettbewerb,
  readonly anzahlKinder: number,
  readonly medianeJeKlassenstufe: Gruppierungsitem[]
};


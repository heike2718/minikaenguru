import { ChartData } from 'chart.js';
import { BAR_BACKGROUND_COLOR_BLUE, WettbewerbOverview } from './domain-model';

export function mapToChartDataJahrAnzahlKinder(wettbewerbe: WettbewerbOverview[]): ChartData<'bar'> {

    /*
    barChartData: ChartData<'bar'> = {
    labels: ['2006', '2007', '2008', '2009', '2010', '2011', '2012'],
    datasets: [
      { data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A' },
      { data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B' },
    ],
  };
  */

    let wettbewerbeReversed: WettbewerbOverview[] = [...wettbewerbe].reverse();

    let labels: string[] = wettbewerbeReversed.map(w => w.jahr + '');
    let data: number[] = wettbewerbeReversed.map(w => w.anzahlKinder);

    // anscheinend wird die borderColor komplett ignoriert
    const result: ChartData<'bar'> = {
        labels: [...labels],
        datasets: [
            {
                data: [...data],
                label: 'Anzahl Kinder',
                backgroundColor: BAR_BACKGROUND_COLOR_BLUE,
                borderColor: 'rgba(0,0,0,1)' // adjustBorderColors(BAR_BACKGROUND_COLOR_BLUE, 0.8)
            }
        ]
    };

    return result;
}



function adjustBorderColors(backgroundColor: string, factor: number): string {

    const rgbValues = backgroundColor.split(',').map(str => parseInt(str.replace(/\D/g, '')));
    const darkerRgbValues = rgbValues.map(value => value * factor); 
    
    console.log(backgroundColor + ' => ' + darkerRgbValues);

    return `rgba(${darkerRgbValues[0]},${darkerRgbValues[1]},${darkerRgbValues[2]},${darkerRgbValues[3]})`
}
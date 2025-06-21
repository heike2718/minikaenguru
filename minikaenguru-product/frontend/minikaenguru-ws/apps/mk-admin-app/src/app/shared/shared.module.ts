import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SchulkatalogdataComponent } from './schulkatalogdata/schulkatalogdata.component';


@NgModule({
    declarations: [
        SchulkatalogdataComponent
    ],
    imports: [
      CommonModule
    ],
    exports: [
        SchulkatalogdataComponent
    ]
  })  
export class SharedModule {}
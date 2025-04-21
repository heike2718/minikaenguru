import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '@mks/messages-api';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'

@Component({
    selector: 'mks-loading-indicator',
    imports: [CommonModule, MatProgressSpinnerModule],
    templateUrl: './loading-indicator.component.html',
    styleUrl: './loading-indicator.component.scss'
})
export class LoadingIndicatorComponent {

  loadingService = inject(LoadingService);

}

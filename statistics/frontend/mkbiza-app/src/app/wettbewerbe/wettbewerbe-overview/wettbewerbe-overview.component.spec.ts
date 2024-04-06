import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WettbewerbeOverviewComponent } from './wettbewerbe-overview.component';

describe('WettbewerbeOverviewComponent', () => {
  let component: WettbewerbeOverviewComponent;
  let fixture: ComponentFixture<WettbewerbeOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WettbewerbeOverviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WettbewerbeOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

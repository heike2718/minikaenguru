import { ComponentFixture, TestBed } from '@angular/core/testing';
import { KlassenstufeDetailsComponent } from './klassenstufe-details.component';

describe('KlassenstufeDetailsComponent', () => {
  let component: KlassenstufeDetailsComponent;
  let fixture: ComponentFixture<KlassenstufeDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KlassenstufeDetailsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(KlassenstufeDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

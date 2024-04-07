import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JahreKinderKlassenstufeComponent } from './jahre-kinder-klassenstufe.component';

describe('JahreKinderKlassenstufeComponent', () => {
  let component: JahreKinderKlassenstufeComponent;
  let fixture: ComponentFixture<JahreKinderKlassenstufeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JahreKinderKlassenstufeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(JahreKinderKlassenstufeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

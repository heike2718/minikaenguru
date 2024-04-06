import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JahreAnzahlKinderComponent } from './jahre-anzahl-kinder.component';

describe('JahreAnzahlKinderComponent', () => {
  let component: JahreAnzahlKinderComponent;
  let fixture: ComponentFixture<JahreAnzahlKinderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JahreAnzahlKinderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(JahreAnzahlKinderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AufgabeDetailsComponent } from './aufgabe-details.component';

describe('AufgabeDetailsComponent', () => {
  let component: AufgabeDetailsComponent;
  let fixture: ComponentFixture<AufgabeDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AufgabeDetailsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AufgabeDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RohpunktitemComponent } from './rohpunktitem.component';

describe('RohpunktitemComponent', () => {
  let component: RohpunktitemComponent;
  let fixture: ComponentFixture<RohpunktitemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RohpunktitemComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RohpunktitemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JahreMedianeComponent } from './jahre-mediane.component';

describe('JahreMedianeComponent', () => {
  let component: JahreMedianeComponent;
  let fixture: ComponentFixture<JahreMedianeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JahreMedianeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(JahreMedianeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

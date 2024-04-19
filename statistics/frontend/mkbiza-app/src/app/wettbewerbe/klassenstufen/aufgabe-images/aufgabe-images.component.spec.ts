import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AufgabeImagesComponent } from './aufgabe-images.component';

describe('AufgabeImagesComponent', () => {
  let component: AufgabeImagesComponent;
  let fixture: ComponentFixture<AufgabeImagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AufgabeImagesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AufgabeImagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

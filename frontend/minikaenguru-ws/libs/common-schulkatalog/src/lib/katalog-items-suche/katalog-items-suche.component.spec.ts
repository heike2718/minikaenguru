import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { KatalogItemsSucheComponent } from './katalog-items-suche.component';

describe('KatalogItemsSucheComponent', () => {
  let component: KatalogItemsSucheComponent;
  let fixture: ComponentFixture<KatalogItemsSucheComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KatalogItemsSucheComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KatalogItemsSucheComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadKlassenlistenComponent } from './upload-klassenlisten.component';

describe('UploadKlassenlistenComponent', () => {
  let component: UploadKlassenlistenComponent;
  let fixture: ComponentFixture<UploadKlassenlistenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadKlassenlistenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadKlassenlistenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

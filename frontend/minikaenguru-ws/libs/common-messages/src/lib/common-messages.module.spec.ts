import { async, TestBed } from '@angular/core/testing';
import { CommonMessagesModule } from './common-messages.module';

describe('CommonMessagesModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [CommonMessagesModule]
    }).compileComponents();
  }));

  it('should create', () => {
    expect(CommonMessagesModule).toBeDefined();
  });
});

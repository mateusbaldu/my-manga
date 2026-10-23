import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuditLog } from './audit-log';

describe('AuditLog', () => {
  let service: AuditLog;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()],
    });
    service = TestBed.inject(AuditLog);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

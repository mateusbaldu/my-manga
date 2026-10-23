import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { AdminAuditLogs } from './admin-audit-logs';

describe('AdminAuditLogs', () => {
  let component: AdminAuditLogs;
  let fixture: ComponentFixture<AdminAuditLogs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminAuditLogs],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminAuditLogs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

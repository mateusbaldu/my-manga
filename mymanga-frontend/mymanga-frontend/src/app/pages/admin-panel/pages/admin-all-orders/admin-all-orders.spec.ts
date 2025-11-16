import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAllOrders } from './admin-all-orders';

describe('AdminAllOrders', () => {
  let component: AdminAllOrders;
  let fixture: ComponentFixture<AdminAllOrders>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminAllOrders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminAllOrders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

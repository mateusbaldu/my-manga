import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminManageMangas } from './admin-manage-mangas';

describe('AdminManageMangas', () => {
  let component: AdminManageMangas;
  let fixture: ComponentFixture<AdminManageMangas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminManageMangas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminManageMangas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

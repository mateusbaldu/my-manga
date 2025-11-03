import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Vitrine } from './vitrine';

describe('Vitrine', () => {
  let component: Vitrine;
  let fixture: ComponentFixture<Vitrine>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Vitrine]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Vitrine);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

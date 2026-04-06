import { TestBed } from '@angular/core/testing';

import { CartService } from './cart-service';

describe('CartService', () => {
  let service: CartService;
  let setItemSpy: jasmine.Spy;
  let getItemSpy: jasmine.Spy;

  beforeEach(() => {
    setItemSpy = spyOn(localStorage, 'setItem');
    getItemSpy = spyOn(localStorage, 'getItem');

    TestBed.configureTestingModule({});
    service = TestBed.inject(CartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not load cart from localStorage on initialization', () => {
    expect(getItemSpy).not.toHaveBeenCalled();
    expect(service.getItems()).toEqual([]);
  });

  it('should not persist cart in localStorage', () => {
    service.clearCart();

    expect(setItemSpy).not.toHaveBeenCalled();
  });
});

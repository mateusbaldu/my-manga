import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { Auth } from './auth';

describe('Auth', () => {
  let service: Auth;
  let localStorageSpy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(Auth);
    localStorageSpy = spyOn(localStorage, 'setItem');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not persist auth data in localStorage on logout', () => {
    service.logout();

    expect(localStorageSpy).not.toHaveBeenCalled();
    expect(service.getToken()).toBeNull();
  });
});

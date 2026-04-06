import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import { Manga } from './manga';

describe('Manga', () => {
  let service: Manga;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj<HttpClient>('HttpClient', ['get', 'post', 'patch', 'delete']);
    httpClientSpy.get.and.returnValue(of({}));
    service = new Manga(httpClientSpy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not persist manga listing queries in localStorage', () => {
    const getItemSpy = spyOn(localStorage, 'getItem');
    const setItemSpy = spyOn(localStorage, 'setItem');
    const removeItemSpy = spyOn(localStorage, 'removeItem');

    service.getAllMangas().subscribe();
    service.getMangas().subscribe();
    service.searchMangas('naruto').subscribe();

    expect(getItemSpy).not.toHaveBeenCalled();
    expect(setItemSpy).not.toHaveBeenCalled();
    expect(removeItemSpy).not.toHaveBeenCalled();
  });
});

import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, of } from 'rxjs';

import { Manga } from './manga';

describe('Manga', () => {
  let service: Manga;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj<HttpClient>('HttpClient', ['get', 'post', 'patch', 'delete']);
    httpClientSpy.get.and.returnValue(of({}));
    TestBed.configureTestingModule({
      providers: [Manga, { provide: HttpClient, useValue: httpClientSpy }],
    });
    service = TestBed.inject(Manga);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not persist manga listing queries in localStorage', async () => {
    const getItemSpy = spyOn(localStorage, 'getItem');
    const setItemSpy = spyOn(localStorage, 'setItem');
    const removeItemSpy = spyOn(localStorage, 'removeItem');

    await firstValueFrom(service.getAllMangas());
    await firstValueFrom(service.getMangas());
    await firstValueFrom(service.searchMangas('naruto'));

    expect(getItemSpy).not.toHaveBeenCalled();
    expect(setItemSpy).not.toHaveBeenCalled();
    expect(removeItemSpy).not.toHaveBeenCalled();
  });
});

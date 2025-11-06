import { TestBed } from '@angular/core/testing';

import { Manga } from './manga';

describe('Manga', () => {
  let service: Manga;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Manga);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

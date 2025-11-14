import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Navigation {
  private homeClickedSource = new Subject<void>();

  homeClicked$ = this.homeClickedSource.asObservable();

  notifyHomeClicked(): void {
    this.homeClickedSource.next();
  }
}

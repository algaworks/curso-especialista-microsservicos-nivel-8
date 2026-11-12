import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MenuDataService {

  constructor() { }

  public toggleMenuBar: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    
}

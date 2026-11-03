import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http: HttpClient) { }

  getProtectedApiResponse(): Observable<string> {
    return this.http.get<any>('http://algashop-admin-gateway:9999/api/v1/products')
      .pipe(
        map(response => response.find((i: any) => i.type === 'iss').value),
        map(iss => '☁ API Success from ' + iss),
        catchError((e: HttpErrorResponse) => of(`🌩 API Error: ${e.status} ${e.statusText}`)),
      );
  }
}

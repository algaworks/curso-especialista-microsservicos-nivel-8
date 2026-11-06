import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InvoiceModel } from './models/model';

@Injectable({
  providedIn: 'root'
})
export class InvoicesService {
  private url: string;

  constructor(private http: HttpClient) {
    this.url = `${environment.apiUrl}/api/v1/orders`;
  }

  getByOrderId(orderId: string): Observable<InvoiceModel> {
    return this.http.get<InvoiceModel>(`${this.url}/${orderId}/invoice`);
  }
}

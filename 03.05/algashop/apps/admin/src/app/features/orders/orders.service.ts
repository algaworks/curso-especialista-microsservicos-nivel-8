import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from 'src/app/core/models';
import { Observable } from 'rxjs';
import { CustomerModel, OrderFilter, OrderModel } from './models/model';

@Injectable({
    providedIn: 'root'
})
export class OrdersService {
    private url: string;

    constructor(private http: HttpClient) {
      this.url = `${environment.apiUrl}/api/v1/orders`;
    }

    filter(filter: OrderFilter = new OrderFilter()): Observable<Page<OrderModel>> {
        let params = filter.toQueryParams();
        return this.http.get<Page<OrderModel>>(`${this.url}`, { params });
    }

    getOne(orderId: string): Observable<OrderModel> {
        return this.http.get<OrderModel>(`${this.url}/${orderId}`);
    }
    getCutomerById(customerId: string): Observable<CustomerModel> {
        return this.http.get<CustomerModel>(`${environment.apiUrl}/api/v1/customers/${customerId}`);
    }

}

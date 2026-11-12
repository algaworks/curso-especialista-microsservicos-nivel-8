import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from 'src/app/core/models';
import { Observable } from 'rxjs';
import { CustomerFilter, CustomerModel } from './models/model';

@Injectable({
    providedIn: 'root'
})
export class CustomersService {
    private url: string;

    constructor(private http: HttpClient) {
      this.url = `${environment.apiUrl}/api/v1/customers`;
    }

    filter(filter: CustomerFilter = new CustomerFilter()): Observable<Page<CustomerModel>> {
        let params = filter.toQueryParams();
        return this.http.get<Page<CustomerModel>>(`${this.url}`, { params });
    }

    getOne(customerId: string): Observable<CustomerModel> {
        return this.http.get<CustomerModel>(`${this.url}/${customerId}`);
    }

}

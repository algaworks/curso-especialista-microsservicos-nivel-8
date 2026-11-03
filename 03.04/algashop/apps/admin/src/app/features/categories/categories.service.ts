import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { CategoryFilter, CategoryInput, CategoryModel, Page } from './models/models';

@Injectable({
    providedIn: 'root'
})
export class CategoriesService {
    private url: string;

    constructor(private http: HttpClient) {
      this.url = `${environment.apiUrl}/api/v1/categories`;
    }

    filter(filter: CategoryFilter = new CategoryFilter()): Observable<Page<CategoryModel>> {
        let params = filter.toQueryParams();
        return this.http.get<Page<CategoryModel>>(`${this.url}`, { params });
    }

    getOne(categoryId: string): Observable<CategoryModel> {
        return this.http.get<CategoryModel>(`${this.url}/${categoryId}`);
    }

    create(categoryInput: CategoryInput): Observable<CategoryModel> {
        return this.http.post<CategoryModel>(`${this.url}`, categoryInput);
    }

    update(categoryId: string, categoryInput: CategoryInput): Observable<CategoryModel> {
        return this.http.put<CategoryModel>(`${this.url}/${categoryId}`, categoryInput)
    }
}

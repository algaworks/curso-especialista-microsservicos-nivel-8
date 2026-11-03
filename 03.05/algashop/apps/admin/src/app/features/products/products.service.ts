import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page, ProductFilter, ProductModel, ProductInput, ImageModel, UploadRequestInput, UploadRequestInputResult, CategoryModel, ImageInput, ProductQuantityInput } from 'src/app/core/models';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ProductsService {
    private url: string;

    constructor(private http: HttpClient) {
      this.url = `${environment.apiUrl}/api/v1/products`;
    }

    filter(filter: ProductFilter = new ProductFilter()): Observable<Page<ProductModel>> {
        let params = filter.toQueryParams();
        return this.http.get<Page<ProductModel>>(`${this.url}`, { params });
    }

    getOne(productId: string): Observable<ProductModel> {
        return this.http.get<ProductModel>(`${this.url}/${productId}`);
    }

    create(productInput: ProductInput): Observable<ProductModel> {
        return this.http.post<ProductModel>(`${this.url}`, productInput);
    }

    update(productId: string, productInput: ProductInput): Observable<ProductModel> {
        return this.http.put<ProductModel>(`${this.url}/${productId}`, productInput)
    }

    restock(productId: string, quantity: number): Observable<void> {
        return this.http.post<void>(`${this.url}/${productId}/restock`, new ProductQuantityInput(quantity));
    }

    withdraw(productId: string, quantity: number): Observable<void> {
        return this.http.post<void>(`${this.url}/${productId}/withdraw`, new ProductQuantityInput(quantity));
    }

    getImages(productId: string) : Observable<ImageModel[]> {
        return this.http.get<ImageModel[]>(`${this.url}/${productId}/images`);
    }

    makeImageAsPrimary(productId: string, imageId: string) : Observable<void> {
        return this.http.put<void>(`${this.url}/${productId}/images/${imageId}/primary`, {});
    }

    deleteImage(productId: string, imageId: string) : Observable<void> {
        return this.http.delete<void>(`${this.url}/${productId}/images/${imageId}`, {});
    }

    requestUpload(input: UploadRequestInput) : Observable<UploadRequestInputResult> {
        return this.http.post<UploadRequestInputResult>(`${environment.apiUrl}/api/v1/upload-requests`, input);
    }

    addImageToProduct(productId: string, input: ImageInput) : Observable<ImageModel> {
        return this.http.post<ImageModel>(`${this.url}/${productId}/images`, input);
    }

    getCategories(): Observable<Page<CategoryModel>> {
        return this.http.get<Page<CategoryModel>>(`${environment.apiUrl}/api/v1/categories`);
    }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Page } from 'src/app/core/models';
import { Observable } from 'rxjs';
import { UserFilter, UserInput, UserModel, UserUpdateInput } from './models/model';

@Injectable({
    providedIn: 'root'
})
export class UsersService {
    private url: string;

    constructor(private http: HttpClient) {
      this.url = `${environment.authUrl}/api/v1/users`;
    }

    filter(filter: UserFilter = new UserFilter()): Observable<Page<UserModel>> {
        let params = filter.toQueryParams();
        return this.http.get<Page<UserModel>>(`${this.url}`, { params });
    }

    getOne(userId: string): Observable<UserModel> {
        return this.http.get<UserModel>(`${this.url}/${userId}`);
    }

    create(userInput: UserInput): Observable<UserModel> {
        return this.http.post<UserModel>(`${this.url}`, userInput);
    }

    update(userId: string, userInput: UserUpdateInput): Observable<UserModel> {
        return this.http.put<UserModel>(`${this.url}/${userId}`, userInput)
    }


}

import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
  })
export class ImageUploadService {

  constructor(private http: HttpClient) {
    
  }

  upload(file: File, uploadSignedUrl: string): Observable<void> {
    return this.http.put<void>(uploadSignedUrl, file);
  }
  
}
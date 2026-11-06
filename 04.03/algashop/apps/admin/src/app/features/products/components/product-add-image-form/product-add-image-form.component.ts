import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { ProductsService } from '../../products.service';
import { FileBeforeUploadEvent, FileSelectEvent, FileUploadErrorEvent, FileUploadEvent, FileUploadHandlerEvent } from 'primeng/fileupload';
import { ImageUploadService } from '../../image-upload.service';
import { ImageInput, UploadRequestInput as PresignedRequest } from 'src/app/core/models';
import { catchError, concatMap, firstValueFrom, tap, throwError } from 'rxjs';

@Component({
  selector: 'app-product-add-image-form',
  templateUrl: './product-add-image-form.component.html',
  styleUrl: './product-add-image-form.component.css'
})
export class ProductAddImageFormComponent implements OnInit {
  productId?: string;
  form?: FormGroup;
  contentTypes = 'image/png,image/jpeg';
  selectedFile?: any;
  saving = false;

  constructor(
    private confirmationService: ConfirmationService,
    private imageUploadService: ImageUploadService,
    private productsService: ProductsService,
    private messageService: MessageService,
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig
  ) { }

  ngOnInit(): void {
    this.productId = this.config?.data['productId'];
  }

  onUpload(event: FileUploadEvent) {

  }

  onBeforeUpload(event: FileBeforeUploadEvent) {

  }

  onSelect(event: FileSelectEvent) {

  }

  onError(event: FileUploadErrorEvent) {

  }

  uploadHandler(event : FileUploadHandlerEvent) {
    const file = event.files[0]!;
    let presignedRequest = new PresignedRequest(file.name, file.size);
    this.saving = true;

    firstValueFrom(
      this.productsService.requestUpload(presignedRequest)
        .pipe(
          concatMap(response => this.imageUploadService.upload(file, response.uploadSignedUrl)
            .pipe(concatMap(() => this.productsService.addImageToProduct(this.productId!, new ImageInput(response.remoteFileName))))
          ),
          catchError(error => {
            return throwError(() => error);
          })
        )
    ).then(() => {
      this.saving = false;
      this.messageService.add({severity:'success', summary: "Success", detail: 'The selected image has been registered.'});
      this.ref.close(true);
    }).catch(error => {
      this.saving = false;
      console.error(error);
      this.messageService.add({severity:'error', summary: "Erro", detail: 'An unknown error occurred while trying to send the image.'});
    });
  }

}

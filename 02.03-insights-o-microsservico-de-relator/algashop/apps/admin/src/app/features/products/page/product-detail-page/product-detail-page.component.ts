import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';

import { sleep } from 'src/app/shared/shared.module';
import { ImageModel, ProductModel } from 'src/app/core/models';
import { ProductsService } from '../../products.service';
import { firstValueFrom } from 'rxjs';
import { ProductAddImageFormComponent } from '../../components/product-add-image-form/product-add-image-form.component';

type StockMovementType = 'restock' | 'withdraw';

@Component({
  selector: 'app-product-detail-page',
  templateUrl: './product-detail-page.component.html',
  styleUrl: './product-detail-page.component.css'
})
export class ProductDetailPageComponent implements OnInit {
  product?: ProductModel;
  images: ImageModel[] = [];
  productId?: string;
  loading = true;
  menuItems: MenuItem[] = [];
  ref?: DynamicDialogRef;

  loadingImages = true;
  error = true;

  displayCustom?: boolean;
  activeIndex: number = 0;

  canShowDescription: boolean = false;
  canShowStockMovementDialog: boolean = false;
  stockMovementType?: StockMovementType;
  savingStockMovement = false;
  stockMovementForm = new FormGroup({
    quantity: new FormControl<number | null>(null, [Validators.required, Validators.min(1)])
  });

  responsiveOptions: any[] = [
    {
        breakpoint: '1500px',
        numVisible: 5
    },
    {
        breakpoint: '1024px',
        numVisible: 3
    },
    {
        breakpoint: '768px',
        numVisible: 2
    },
    {
        breakpoint: '560px',
        numVisible: 1
    }
  ];

  carouselResponsiveOptions: any[] = [
    {
        breakpoint: '1024px',
        numVisible: 3,
        numScroll: 3
    },
    {
        breakpoint: '768px',
        numVisible: 2,
        numScroll: 2
    },
    {
        breakpoint: '560px',
        numVisible: 1,
        numScroll: 1
    }
  ];


  constructor(
    private productsService: ProductsService,
    private route: ActivatedRoute,
    private router: Router,
    private dialogService: DialogService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
    ) { }

  ngOnInit(): void {
    this.productId = this.route.snapshot.params['productId'];
    this.loadData();
    this.setupMenus();
  }

  imageClick(selectedImage: ImageModel) {
    for(let i = 0; i < this.images.length; i++) {
      if (this.images[i].id == selectedImage.id) {
        this.activeIndex = i;
        this.displayCustom = true;
        return;
      }
    }
  }

  makeImagePrimary(event: Event, imageId: string) {
      this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Are you sure you want to set this image as the main image of the product?',
        header: 'Confirmation',
        icon: 'pi pi-exclamation-triangle',
        acceptIcon:"none",
        rejectIcon:"none",
        rejectButtonStyleClass:"p-button-text",
        accept: () => {
            firstValueFrom(this.productsService.makeImageAsPrimary(this.productId!, imageId))
              .then(()=> {
                this.loadData();
                this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'The image is now the main image of this product.', life: 3000 });
              })
              .catch((e: any) => {
                console.error(e);
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'It was not possible to make this image the main one.', life: 3000 });
              })
        }
    });
  }

  deleteImage(event: Event, imageId: string) {
      this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Are you sure you want to delete this image? This is an irreversible process!',
        header: 'Confirmation',
        icon: 'pi pi-info-circle',
        acceptButtonStyleClass:"p-button-danger p-button-text",
        rejectButtonStyleClass:"p-button-text p-button-text",
        acceptIcon:"none",
        rejectIcon:"none",
        accept: () => {
          firstValueFrom(this.productsService.deleteImage(this.productId!, imageId))
            .then(()=> {
              this.loadData();
              this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Image deleted', life: 3000 });
            })
            .catch((e: any) => {
              console.error(e);
              this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Image not deleted', life: 3000 });
            })

        }
    });
  }

  imageSelected(index: number) {
    this.activeIndex = index;
    this.displayCustom = true;
  }

  showDescription() {
    this.canShowDescription = true;
  }

  get stockMovementDialogTitle(): string {
    return this.stockMovementType === 'withdraw' ? 'Remove inventory' : 'Add inventory';
  }

  showStockMovementDialog(type: StockMovementType) {
    this.stockMovementType = type;
    this.stockMovementForm.reset();
    this.canShowStockMovementDialog = true;
  }

  cancelStockMovement() {
    if (this.savingStockMovement) {
      return;
    }

    this.canShowStockMovementDialog = false;
    this.resetStockMovementDialog();
  }

  confirmStockMovement() {
    if (this.stockMovementForm.invalid || !this.stockMovementType) {
      this.stockMovementForm.markAllAsTouched();
      return;
    }

    const quantity = Number(this.stockMovementForm.controls.quantity.value);
    const request = this.stockMovementType === 'withdraw'
      ? this.productsService.withdraw(this.productId!, quantity)
      : this.productsService.restock(this.productId!, quantity);

    this.savingStockMovement = true;
    firstValueFrom(request)
      .then(() => {
        this.savingStockMovement = false;
        this.canShowStockMovementDialog = false;
        this.resetStockMovementDialog();
        this.loadProduct();
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Inventory updated',
          life: 3000
        });
      })
      .catch((e: any) => {
        console.error(e);
        this.savingStockMovement = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'It was not possible to update the inventory.',
          life: 3000
        });
      });
  }

  resetStockMovementDialog() {
    if (this.savingStockMovement) {
      return;
    }

    this.stockMovementType = undefined;
    this.stockMovementForm.reset();
  }

  addNewImage() {
    this.ref = this.dialogService.open(ProductAddImageFormComponent, {
      data: {
        productId: this.productId!
      },
      header: 'Add image',
      width: '50%',
      contentStyle: {"max-height": "500px", "overflow": "auto"},
      baseZIndex: 10000
    });

    this.ref.onClose.subscribe((changed) =>{
      if (changed == true) {
        this.loading = true;
        this.loadData();
      }
    });
  }

  private loadData() {
    this.loading = true; // on page refresh
    sleep()
      .then(()=> {
        this.loadProduct();
        this.loadProductImages();
      });
  }

  private setupMenus() {
    this.menuItems = this.menuItems = [
      {
        label: 'Options',
        items: [
          {
            label: 'Edit',
            command: () => {
              this.edit();
            }
          },
          {
            label: 'Add inventory',
            command: () => {
              this.showStockMovementDialog('restock');
            }
          },
          {
            label: 'Remove inventory',
            command: () => {
              this.showStockMovementDialog('withdraw');
            }
          }
        ]
      }
    ];
  }

  private edit() {
    if (this.productId) {
      this.router.navigate([`/products/${this.productId}/edit`])
    }
  }

  private loadProduct() {
    firstValueFrom(this.productsService.getOne(this.productId!))
      .then(product => {
        this.product = product;
        this.loading = false;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Connection error with the server',
            detail: 'Unable to retrieve the product',
            boolean: true
        }
        this.messageService.add(message);
      });
  }

  private loadProductImages() {
    this.loadingImages = true;
    this.images = [];
    firstValueFrom(this.productsService.getImages(this.productId!))
      .then(images => {
        this.images = images;
        this.loadingImages = false;
      })
      .catch(e => {
        console.error(e);
        let message = {
            severity:'error',
            summary: 'Connection error with the server',
            detail: 'Unable to retrieve the list of images',
            boolean: true
        }
        this.messageService.add(message);
        this.loadingImages = false;
        this.error = true;
      });
  }

}

import { HttpParams } from "@angular/common/http";

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

export function applySortParams(
    params: HttpParams,
    sort?: string
): HttpParams {
    if (!sort) {
        return params;
    }

    const [rawProperty, rawDirection = 'ASC'] = sort.split(',');
    const property = rawProperty?.trim();
    if (!property) {
        return params;
    }

    const direction = rawDirection.trim().toUpperCase() === 'DESC' ? 'DESC' : 'ASC';

    return params
            .set('sortByProperty', property)
            .set('sortDirection', direction);
}

export enum ProductSortProperty {
    ADDED_AT = 'ADDED_AT',
    SALE_PRICE = 'SALE_PRICE',
}

export class PageRequest {
    constructor(
        public order?: string,
        public direction?: string,
        public pageNumber: number = 0,
        public recordsPerPage: number = 20,
    ) {

    }

    applyParams(params?: any) {
        if (!params) {
            return;
        }

        if (params.direction) {
            this.direction = params.direction
        }

        if (params.recordsPerPage) {
            this.recordsPerPage = params.recordsPerPage
        }

        if (params.order) {
            this.order = params.order
        }

        if (params.pageNumber) {
            this.pageNumber = params.pageNumber
        }
    }

    applySort(sortField?: string, sortOrder: number = 1) {
        this.order = sortField;
        this.direction = sortOrder == 1 ? 'ASC' : 'DESC';
    }
}

export class ProductFilter {
    order?: string;
    direction?: string;
    page: number = 0;
    size: number = 10;
    sort: string = `${ProductSortProperty.ADDED_AT},ASC`;

    enabled?: boolean;
    inventoryStatus?: string;
    inStock?: boolean;
    hasDiscount?: boolean;
    term?: string;
    categoriesId?: string[];
    priceFrom?: number;
    priceTo?: number;
    addedAtFrom?: string;
    addedAtTo?: string;

    constructor(data?: {}) {
      // this.applyParams(data);
    }

    applyParams(params?: any) {
        Object.assign(this, params);
    }

    applyPage(params?: any) {
        if (!params) {
            return;
        }

        if (params.direction) {
            this.direction = params.direction
        }

        if (params.size) {
            this.size = params.size
        }

        if (params.order) {
            this.order = params.order
        }

        if (params.page) {
            this.page = params.page
        }
    }

    isEmpty() : boolean {
      if (this.term) {
        return false
      }

      return true;
    }

    isNotEmpty() : boolean {
      return !this.isEmpty();
    }

    toQueryParams() : HttpParams {
        let params = new HttpParams()
            .set('page', this.page)
            .set('size', this.size);

        if(this.term) {
            params = params.set('term', this.term);
        }
        if(this.enabled !== undefined && this.enabled !== null) {
          params = params.set('enabled', this.enabled);
        }
        if(this.inStock !== undefined && this.inStock !== null) {
          params = params.set('inStock', this.inStock);
        }
        if(this.hasDiscount !== undefined && this.hasDiscount !== null) {
          params = params.set('hasDiscount', this.hasDiscount);
        }
        if(this.priceFrom !== undefined && this.priceFrom !== null) {
          params = params.set('priceFrom', this.priceFrom);
        }
        if(this.priceTo !== undefined && this.priceTo !== null) {
          params = params.set('priceTo', this.priceTo);
        }
        if(this.categoriesId) {
          params = params.set('categoriesId', this.categoriesId.join(","));
        }
        if(this.addedAtFrom) {
          params = params.set('addedAtFrom', this.addedAtFrom);
        }
        if(this.addedAtTo) {
          params = params.set('addedAtTo', this.addedAtTo);
        }

        return applySortParams(params, this.sort);
    }
}

export interface ProductModel {
    id: string;
    name: string;
    brand: string;
    enabled: boolean;
    regularPrice: number;
    salePrice: number;
    hasDiscount: boolean;
    category: CategoryModel;
    mainImage: ImageModel;
    description: string;
    quantityInStock: number;

    //read only
    addedAt: Date;
    updatedAt: Date;
    inStock: boolean;
    discountPercentageRounded: number;
    slug: string;
    price: number;
}

export interface ImageModel {
    id: string;
    url: string;
}

export interface CategoryModel {
    id: string;
    name: string;
}

export enum ProductInventoryStatus {
    OUT_OF_STOCK = "Sem estoque",
    IN_STOCK = "Com estoque",
}


export class ProductInput {
    constructor(
        public name: string,
        public brand: string,
        public enabled: boolean,
        public regularPrice: number,
        public salePrice: number,
        public description: string,
        public categoryId: string
    ) {}
}

export class UploadRequestInput {
    constructor(
      public originalFileName: string,
      public contentLength: number
    ) {}
}

export class UploadRequestInputResult {
    constructor(
      public remoteFileName: string,
      public contentLength: number,
      public contentType: string,
      public uploadSignedUrl: string,
      public expiresAt: string
    ) {}
}

export class ImageInput {
    constructor(
        public remoteFileName: string
    ) {}
}

export class ProductQuantityInput {
    constructor(
        public quantity: number
    ) {}
}

export class ProductImagesInput {
    constructor(
        public ids: number[] = []
    ) {}
}


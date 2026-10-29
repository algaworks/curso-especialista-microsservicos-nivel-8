import { HttpParams } from "@angular/common/http";
import { applySortParams } from "src/app/core/models";

export enum CategorySortProperty {
    NAME = 'NAME',
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
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

export class CategoryFilter {
    order?: string;
    direction?: string;
    page: number = 0;
    size: number = 10;
    sort: string = `${CategorySortProperty.NAME},ASC`;

    name?: string;
    enabled?: boolean;

    constructor(data?: {}) {
      this.applyParams(data);
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
    
    toQueryParams() : HttpParams {
        let params = new HttpParams()
            .set('page', this.page)
            .set('size', this.size);

        if(this.name) {
            params = params.set('name', this.name);
        }
        if(this.enabled !== undefined && this.enabled !== null) {
            params = params.set('enabled', this.enabled);
        }

        return applySortParams(params, this.sort);
    }
}

export interface CategoryModel {
    id: string;
    name: string;
    enabled: boolean;
    slug: string;
}

export class CategoryInput {
    constructor(
        public name: string,
        public enabled: boolean,
    ) {}
}








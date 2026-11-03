import { HttpParams } from "@angular/common/http";
import { applySortParams } from "src/app/core/models";

export interface UserModel {
  id: string;
  name: string;
  email: string;
  enabled: boolean;
  type: UserType;
}

export enum UserSortProperty {
  NAME = 'NAME',
  EMAIL = 'EMAIL',
  ENABLED = 'ENABLED',
  TYPE = 'TYPE',
}

export class UserFilter {
  order?: string;
  direction?: string;
  page: number = 0;
  size: number = 10;
  sort: string = `${UserSortProperty.NAME},ASC`;

  email?: string;
  type?: any;

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

  toQueryParams() : HttpParams {
      let params = new HttpParams()
          .set('page', this.page)
          .set('size', this.size);

      if(this.email){
        params = params.set('email', this.email);
      }

      if(this.type){
        params = params.set('type', this.type.value || this.type);
      }

      return applySortParams(params, this.sort);
  }
}

export class UserInput {
  constructor(
      public name: string,
      public email: string,
      public type: UserType
  ) {}
}

export class UserUpdateInput {
  constructor(
      public name: string,
      public type: UserType,
      public enabled: boolean
  ) {}
}

export enum UserType {
  MANAGER = "Manager",
  OPERATOR = "Operator",
}

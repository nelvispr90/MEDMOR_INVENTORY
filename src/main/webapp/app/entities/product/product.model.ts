import { IRegistry } from 'app/entities/registry/registry.model';
import { ProductSize } from 'app/entities/enumerations/product-size.model';
import { ContainerType } from 'app/entities/enumerations/container-type.model';

export interface IProduct {
  id?: number;
  productSize?: ProductSize;
  color?: string;
  price?: number;
  fragile?: boolean;
  lote?: string;
  containerType?: ContainerType;
  registries?: IRegistry[] | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public productSize?: ProductSize,
    public color?: string,
    public price?: number,
    public fragile?: boolean,
    public lote?: string,
    public containerType?: ContainerType,
    public registries?: IRegistry[] | null
  ) {
    this.fragile = this.fragile ?? false;
  }
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}

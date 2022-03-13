import { IProduct } from 'app/entities/product/product.model';
import { ISection } from 'app/entities/section/section.model';

export interface IRegistry {
  id?: number;
  amount?: number;
  product?: IProduct;
  section?: ISection;
}

export class Registry implements IRegistry {
  constructor(public id?: number, public amount?: number, public product?: IProduct, public section?: ISection) {}
}

export function getRegistryIdentifier(registry: IRegistry): number | undefined {
  return registry.id;
}

import { IRegistry } from 'app/entities/registry/registry.model';
import { ProductType } from 'app/entities/enumerations/product-type.model';

export interface ISection {
  id?: number;
  area?: number;
  productType?: ProductType;
  registries?: IRegistry[] | null;
}

export class Section implements ISection {
  constructor(public id?: number, public area?: number, public productType?: ProductType, public registries?: IRegistry[] | null) {}
}

export function getSectionIdentifier(section: ISection): number | undefined {
  return section.id;
}

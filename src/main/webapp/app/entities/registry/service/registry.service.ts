import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRegistry, getRegistryIdentifier } from '../registry.model';

export type EntityResponseType = HttpResponse<IRegistry>;
export type EntityArrayResponseType = HttpResponse<IRegistry[]>;

@Injectable({ providedIn: 'root' })
export class RegistryService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/registries');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(registry: IRegistry): Observable<EntityResponseType> {
    return this.http.post<IRegistry>(this.resourceUrl, registry, { observe: 'response' });
  }

  update(registry: IRegistry): Observable<EntityResponseType> {
    return this.http.put<IRegistry>(`${this.resourceUrl}/${getRegistryIdentifier(registry) as number}`, registry, { observe: 'response' });
  }

  partialUpdate(registry: IRegistry): Observable<EntityResponseType> {
    return this.http.patch<IRegistry>(`${this.resourceUrl}/${getRegistryIdentifier(registry) as number}`, registry, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRegistry>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRegistry[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRegistryToCollectionIfMissing(registryCollection: IRegistry[], ...registriesToCheck: (IRegistry | null | undefined)[]): IRegistry[] {
    const registries: IRegistry[] = registriesToCheck.filter(isPresent);
    if (registries.length > 0) {
      const registryCollectionIdentifiers = registryCollection.map(registryItem => getRegistryIdentifier(registryItem)!);
      const registriesToAdd = registries.filter(registryItem => {
        const registryIdentifier = getRegistryIdentifier(registryItem);
        if (registryIdentifier == null || registryCollectionIdentifiers.includes(registryIdentifier)) {
          return false;
        }
        registryCollectionIdentifiers.push(registryIdentifier);
        return true;
      });
      return [...registriesToAdd, ...registryCollection];
    }
    return registryCollection;
  }
}

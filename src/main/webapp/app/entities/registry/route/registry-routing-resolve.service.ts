import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRegistry, Registry } from '../registry.model';
import { RegistryService } from '../service/registry.service';

@Injectable({ providedIn: 'root' })
export class RegistryRoutingResolveService implements Resolve<IRegistry> {
  constructor(protected service: RegistryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRegistry> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((registry: HttpResponse<Registry>) => {
          if (registry.body) {
            return of(registry.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Registry());
  }
}

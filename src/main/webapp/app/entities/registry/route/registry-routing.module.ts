import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RegistryComponent } from '../list/registry.component';
import { RegistryDetailComponent } from '../detail/registry-detail.component';
import { RegistryUpdateComponent } from '../update/registry-update.component';
import { RegistryRoutingResolveService } from './registry-routing-resolve.service';

const registryRoute: Routes = [
  {
    path: '',
    component: RegistryComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RegistryDetailComponent,
    resolve: {
      registry: RegistryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RegistryUpdateComponent,
    resolve: {
      registry: RegistryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RegistryUpdateComponent,
    resolve: {
      registry: RegistryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(registryRoute)],
  exports: [RouterModule],
})
export class RegistryRoutingModule {}

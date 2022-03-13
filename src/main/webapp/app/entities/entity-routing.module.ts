import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'section',
        data: { pageTitle: 'medmorInventoryApp.section.home.title' },
        loadChildren: () => import('./section/section.module').then(m => m.SectionModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'medmorInventoryApp.product.home.title' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'registry',
        data: { pageTitle: 'medmorInventoryApp.registry.home.title' },
        loadChildren: () => import('./registry/registry.module').then(m => m.RegistryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

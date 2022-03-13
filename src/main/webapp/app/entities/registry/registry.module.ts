import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { RegistryComponent } from './list/registry.component';
import { RegistryDetailComponent } from './detail/registry-detail.component';
import { RegistryUpdateComponent } from './update/registry-update.component';
import { RegistryDeleteDialogComponent } from './delete/registry-delete-dialog.component';
import { RegistryRoutingModule } from './route/registry-routing.module';

@NgModule({
  imports: [SharedModule, RegistryRoutingModule],
  declarations: [RegistryComponent, RegistryDetailComponent, RegistryUpdateComponent, RegistryDeleteDialogComponent],
  entryComponents: [RegistryDeleteDialogComponent],
})
export class RegistryModule {}

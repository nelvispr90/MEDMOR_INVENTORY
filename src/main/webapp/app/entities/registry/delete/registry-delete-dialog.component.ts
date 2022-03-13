import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRegistry } from '../registry.model';
import { RegistryService } from '../service/registry.service';

@Component({
  templateUrl: './registry-delete-dialog.component.html',
})
export class RegistryDeleteDialogComponent {
  registry?: IRegistry;

  constructor(protected registryService: RegistryService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.registryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}

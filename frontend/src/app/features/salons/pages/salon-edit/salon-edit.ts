import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { SalonApiService } from '../../../../core/services/salon-api.service';
import { SalonDetail } from '../../models/salon-detail.model';
import { SalonUpdateRequest } from '../../models/salon-update-request.model';

@Component({
  selector: 'app-salon-edit',
  imports: [RouterLink],
  templateUrl: './salon-edit.html',
  styleUrl: './salon-edit.scss'
})
export class SalonEditPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly salonApi = inject(SalonApiService);

  protected readonly salon = signal<SalonDetail | null>(null);
  protected readonly name = signal('');
  protected readonly address = signal('');
  protected readonly website = signal('');
  protected readonly isLoading = signal(true);
  protected readonly isSaving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (Number.isNaN(id) || id <= 0) {
      this.errorMessage.set('Invalid salon id.');
      this.isLoading.set(false);
      return;
    }

    this.salonApi.getSalonById(id).subscribe({
      next: (salon) => {
        this.salon.set(salon);
        this.name.set(salon.name);
        this.address.set(salon.address);
        this.website.set(salon.website || '');
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load salon details.');
        this.isLoading.set(false);
      }
    });
  }

  protected onNameChange(value: string): void {
    this.name.set(value);
  }

  protected onAddressChange(value: string): void {
    this.address.set(value);
  }

  protected onWebsiteChange(value: string): void {
    this.website.set(value);
  }

  protected saveChanges(): void {
    const currentSalon = this.salon();

    if (!currentSalon) {
      return;
    }

    this.isSaving.set(true);
    this.errorMessage.set(null);

    const request: SalonUpdateRequest = {
      name: this.name().trim(),
      address: this.address().trim(),
      district: currentSalon.district,
      phoneNumber: currentSalon.phoneNumber,
      website: this.website().trim() || null,
      services: currentSalon.services,
      priceRange: currentSalon.priceRange,
      rating: currentSalon.rating,
      reviewCount: currentSalon.reviewCount
    };

    this.salonApi.updateSalon(currentSalon.id, request).subscribe({
      next: (updatedSalon) => {
        this.router.navigate(['/salons', updatedSalon.id]);
      },
      error: () => {
        this.errorMessage.set('Could not save changes.');
        this.isSaving.set(false);
      }
    });
  }
}

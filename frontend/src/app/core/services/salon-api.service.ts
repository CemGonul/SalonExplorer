import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { SalonDetail } from '../../features/salons/models/salon-detail.model';
import { SalonListItem } from '../../features/salons/models/salon-list-item.model';
import { SalonUpdateRequest } from '../../features/salons/models/salon-update-request.model';

export interface SalonListFilters {
  district?: string;
  sortBy?: string;
  direction?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SalonApiService {
  private readonly apiUrl = '/api/salons';

  constructor(private readonly http: HttpClient) {
  }

  getSalons(filters: SalonListFilters = {}): Observable<SalonListItem[]> {
    let params = new HttpParams();

    if (filters.district) {
      params = params.set('district', filters.district);
    }
    if (filters.sortBy) {
      params = params.set('sortBy', filters.sortBy);
    }
    if (filters.direction) {
      params = params.set('direction', filters.direction);
    }

    return this.http.get<SalonListItem[]>(this.apiUrl, { params });
  }

  getSalonById(id: number): Observable<SalonDetail> {
    return this.http.get<SalonDetail>(`${this.apiUrl}/${id}`);
  }

  updateSalon(id: number, request: SalonUpdateRequest): Observable<SalonDetail> {
    return this.http.put<SalonDetail>(`${this.apiUrl}/${id}`, request);
  }
}

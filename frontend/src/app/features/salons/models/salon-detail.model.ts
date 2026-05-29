export interface SalonDetail {
  id: number;
  name: string;
  address: string;
  district: string;
  phoneNumber: string | null;
  website: string | null;
  services: string | null;
  priceRange: string | null;
  rating: number | null;
  reviewCount: number | null;
}

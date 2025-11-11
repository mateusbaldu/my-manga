export interface UserResponse {
  id: number;
  name: string;
  username: string;
  createdAt: string;
  roles: { id: number, name: string }[];
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../services/user';
import { Address } from '../../services/address';
import { Auth } from '../../services/auth';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  userProfile: any = null;
  addresses: any[] = [];
  addressForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  showAddressForm = false;

  constructor(
    private userService: User,
    private addressService: Address,
    private authService: Auth,
    private fb: FormBuilder
  ) {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      number: ['', Validators.required],
      complement: [''],
      neighborhood: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const token = this.authService.getToken();
    if (token) {
      const decoded: any = jwtDecode(token);
      const username = decoded.sub;

      this.loading = true;
      
      this.userService.getProfile(username).subscribe({
        next: (data: any) => {
          this.userProfile = data;
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Erro ao carregar perfil:', err);
          this.errorMessage = 'Erro ao carregar perfil do usuário';
          this.loading = false;
        }
      });

      this.loadAddresses(username);
    }
  }

  loadAddresses(username: string): void {
    this.addressService.getAddresses(username).subscribe({
      next: (data: any) => {
        this.addresses = data;
      },
      error: (err: any) => {
        console.error('Erro ao carregar endereços:', err);
      }
    });
  }

  toggleAddressForm(): void {
    this.showAddressForm = !this.showAddressForm;
    if (!this.showAddressForm) {
      this.addressForm.reset();
      this.successMessage = '';
      this.errorMessage = '';
    }
  }

  addAddress(): void {
    if (this.addressForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }

    const token = this.authService.getToken();
    if (token) {
      const decoded: any = jwtDecode(token);
      const username = decoded.sub;

      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.addressService.addAddress(username, this.addressForm.value).subscribe({
        next: (response: any) => {
          console.log('Endereço adicionado:', response);
          this.successMessage = 'Endereço adicionado com sucesso!';
          this.loading = false;
          this.addressForm.reset();
          this.showAddressForm = false;
          
          this.loadAddresses(username);
        },
        error: (err: any) => {
          console.error('Erro ao adicionar endereço:', err);
          this.errorMessage = 'Erro ao adicionar endereço. Tente novamente.';
          this.loading = false;
        }
      });
    }
  }
}

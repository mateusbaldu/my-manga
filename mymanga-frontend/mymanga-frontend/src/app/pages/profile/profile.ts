import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../../services/user';
import { Address } from '../../services/address';
import { Auth } from '../../services/auth';
import { jwtDecode } from 'jwt-decode';
import { UserResponse } from '../../models/user-response.model';
import { Address as AddressModel } from '../../models/address.model';

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
  userProfile: UserResponse | null = null;
  addresses: AddressModel[] = [];
  addressForm: FormGroup;
  profileForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  userRole: string = 'Carregando...';
  
  
  constructor(
    private userService: User,
    private addressService: Address,
    private authService: Auth,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.addressForm = this.fb.group({
      cep: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      number: ['', Validators.required],
      complement: ['']
    });

    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      username: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const username = this.authService.getUsernameFromToken();
    
    if (username) {
      this.loading = true;
      
      if (this.authService.hasRole('ADMIN')) {
        this.userRole = 'Administrador';
      } else {
        this.userRole = 'Usuário';
      }
      
      this.userService.getProfile(username).subscribe({
        next: (data: UserResponse) => {
          this.userProfile = data;
          this.profileForm.patchValue({
            name: data.name,
            username: data.username
          });
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Erro detalhado:', err);
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao carregar perfil do usuário';
          }
          this.loading = false;
        }
      });

      this.loadAddresses(username);
    }
  }

  loadAddresses(username: string): void {
    this.addressService.getAddresses(username).subscribe({
      next: (data: any) => {
        this.addresses = data.content;
      },
      error: (err: any) => {
        console.error('Erro detalhado:', err);
        if (err.error && err.error.message) {
          if (err.error.errors && Array.isArray(err.error.errors)) {
            this.errorMessage = err.error.errors[0].message;
          } else {
            this.errorMessage = err.error.message;
          }
        } else {
          this.errorMessage = 'Erro ao carregar endereços.';
        }
      }
    });
  }

  addAddress(): void {
    if (this.addressForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }

    const username = this.authService.getUsernameFromToken();
    
    if (username) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.addressService.addAddress(username, this.addressForm.value).subscribe({
        next: (response: AddressModel) => {
          console.log('Endereço adicionado:', response);
          this.successMessage = 'Endereço adicionado com sucesso!';
          this.loading = false;
          this.addressForm.reset();
          
          this.loadAddresses(username); 
        },
        error: (err: any) => {
          console.error('Erro detalhado:', err);
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao adicionar endereço. Tente novamente.';
          }
          this.loading = false;
        }
      });
    }
  }

  onDeleteAddress(addressId: number): void {
    if (!confirm('Tem certeza que deseja excluir este endereço?')) {
      return;
    }

    const username = this.authService.getUsernameFromToken();
    
    if (username) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.addressService.deleteAddress(username, addressId).subscribe({
        next: () => {
          this.successMessage = 'Endereço excluído com sucesso!';
          this.addresses = this.addresses.filter(addr => addr.id !== addressId);
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Erro detalhado:', err);
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao excluir endereço. Tente novamente.';
          }
          this.loading = false;
        }
      });
    }
  }

  onEditAddress(address: AddressModel): void {
    this.addressForm.patchValue({
      cep: address.cep,
      number: address.number,
      complement: address.complement
    });
    this.successMessage = 'Endereço carregado no formulário. Edite e salve.';
    
    setTimeout(() => {
      this.successMessage = '';
    }, 3000);
  }

  onUpdateProfile(): void {
    if (this.profileForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }

    const username = this.authService.getUsernameFromToken();
    
    if (username) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const updateData = {
        name: this.profileForm.value.name,
        username: this.profileForm.value.username
      };

      this.userService.updateProfile(username, updateData).subscribe({
        next: (response: UserResponse) => {
          console.log('Perfil atualizado:', response);
          this.successMessage = 'Perfil atualizado com sucesso!';
          this.userProfile = response;
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Erro detalhado:', err);
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao atualizar perfil. Tente novamente.';
          }
          this.loading = false;
        }
      });
    }
  }

  onDeleteAccount(): void {
    if (!confirm('Tem certeza que deseja DELETAR sua conta? Esta ação é IRREVERSÍVEL!')) {
      return;
    }

    if (!this.userProfile || !this.userProfile.id) {
      this.errorMessage = 'Erro: ID do usuário não encontrado.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.userService.deleteAccount(this.userProfile.id).subscribe({
      next: () => {
        console.log('Conta deletada com sucesso');
        this.authService.logout();
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        console.error('Erro detalhado:', err);
        if (err.error && err.error.message) {
          if (err.error.errors && Array.isArray(err.error.errors)) {
            this.errorMessage = err.error.errors[0].message;
          } else {
            this.errorMessage = err.error.message;
          }
        } else {
          this.errorMessage = 'Erro ao deletar conta. Tente novamente.';
        }
        this.loading = false;
      }
    });
  }
}

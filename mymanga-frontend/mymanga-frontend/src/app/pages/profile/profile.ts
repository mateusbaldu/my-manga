import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../services/user';
import { Address } from '../../services/address';
import { Auth } from '../../services/auth';
import { jwtDecode } from 'jwt-decode';
import { UserResponse } from '../../models/user-response.model';
import { Address as AddressModel } from '../../models/address.model'; // Renomeando o import

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
  loading = false;
  errorMessage = '';
  successMessage = '';
  userRole: string = 'Carregando...';
  
  
  constructor(
    private userService: User,
    private addressService: Address,
    private authService: Auth,
    private fb: FormBuilder
  ) {
    this.addressForm = this.fb.group({
      // Corrigido para bater com o HTML e o DTO AddressCreate
      cep: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      number: ['', Validators.required],
      complement: ['']
    });
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const username = this.authService.getUsernameFromToken(); // Usando o método do Auth
    
    if (username) {
      this.loading = true;
      
      // Determina o papel do usuário
      if (this.authService.hasRole('ADMIN')) {
        this.userRole = 'Administrador';
      } else {
        this.userRole = 'Usuário';
      }
      
      this.userService.getProfile(username).subscribe({
        next: (data: UserResponse) => {
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
        this.addresses = data.content;
      },
      error: (err: any) => {
        console.error('Erro ao carregar endereços:', err);
        this.errorMessage = 'Erro ao carregar endereços.';
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
          
          // Recarrega a lista de endereços
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

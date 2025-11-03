import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css'
})
export class CadastroComponent {
  public name: string = "";
  public email: string = "";
  public username: string = "";
  public password: string = "";
  
  public confirmPassword: string = "";
  public alerta: string = "";

  public cadastrar() {
    if (!this.name || !this.email || !this.username || !this.password || !this.confirmPassword) {
      this.alerta = "Por favor, preencha todos os campos.";
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.alerta = "As senhas não conferem.";
      return;
    }

    const user = {
      name: this.name,
      email: this.email,
      username: this.username,
      password: this.password
    };

    this.alerta = "Cadastro realizado com sucesso!";
    console.log("Dados do usuário para a API:", user);
    
  }
}
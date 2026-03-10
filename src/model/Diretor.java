package model;

//Um Diretor também pode ser visto como um tipo de Funcionário para fins de login.
//Herdar de Funcionario simplifica o gerenciamento de sessão.
public class Diretor extends Funcionario {
 
 // No nosso modelo, um Diretor não pertence a um departamento específico,
 // então o campo 'departamento' herdado de Funcionário pode ser nulo para um Diretor.
 public Diretor() {
     super();
 }
}